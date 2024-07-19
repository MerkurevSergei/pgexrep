package com.github.merkurevsergei.pgexrep;

import com.github.merkurevsergei.pgexrep.decoder.pgoutput.PgOutput;
import jakarta.annotation.PostConstruct;
import org.postgresql.PGConnection;
import org.postgresql.PGProperty;
import org.postgresql.replication.PGReplicationStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
public class LogicalReplicationExample {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @PostConstruct
//    public void setupLogicalReplication() {
//        try {
//            // Устанавливаем логическую репликацию
//            try (Statement stmt = jdbcTemplate.getDataSource().getConnection().createStatement()) {
//              // String sql = "DROP SUBSCRIPTION pgexrep_public";
//                String sql = "CREATE SUBSCRIPTION pgexrep_public " +
//                        "CONNECTION 'host=localhost dbname=pgexrep user=postgres password=postgres' " +
//                        "PUBLICATION pgexrep_public " +
//                        "WITH (slot_name = 'pgexrep_public', create_slot = false)";
//                stmt.execute(sql);
//                System.out.println("Логическая репликация настроена успешно.");
//            } catch (SQLException e) {
//                System.err.println("Ошибка при настройке логической репликации: " + e.getMessage());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
//            // Создаем слот репликации
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pgexrep_public")) {
//                stmt.execute();
//                System.out.println("Слот логической репликации создан успешно.");
//            } catch (SQLException e) {
//                System.err.println("Ошибка при создании слота логической репликации: " + e.getMessage());
//            }
//
//            // Цикл для получения изменений
//            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pg_logical_slot_get_changes('pgexrep_public', NULL, NULL)")) {
//                while (true) {
//                    try (ResultSet rs = stmt.executeQuery()) {
//                        while (rs.next()) {
//                            // Обработка полученных данных
//                            String change = rs.getString("data");
//                            System.out.println("Получено изменение: " + change);
//                            // Добавьте свою логику обработки изменений здесь
//                        }
//                    }
//                    Thread.sleep(1000); // Пауза для уменьшения нагрузки на процессор
//                }
//            } catch (SQLException | InterruptedException e) {
//                System.err.println("Ошибка при получении изменений из слота логической репликации: " + e.getMessage());
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Ошибка при соединении с базой данных: " + e.getMessage());
//        }
//    }

    @PostConstruct
    public void startReplication() throws SQLException, InterruptedException {
//        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
//            PGConnection pgConnection = connection.unwrap(PGConnection.class);
//
//            PGReplicationStream stream = pgConnection.getReplicationAPI()
//                    .replicationStream()
//                    .logical()
//                    .withSlotName("pgexrep_public")
//                    .withSlotOption("include-xids", false)
//                    .withSlotOption("skip-empty-xacts", true)
//                    .withSlotOption("proto_version", 1)
//                    .withStatusInterval(20, TimeUnit.SECONDS)
//                    .start();
//
//            while (true) {
//                ByteBuffer buffer = stream.readPending();
//
//                if (buffer != null) {
//                    int offset = buffer.arrayOffset();
//                    byte[] source = buffer.array();
//                    int length = source.length - offset;
//
//                    String data = new String(source, offset, length);
//                    System.out.println("Received data: " + data);
//
//                    // Confirm receipt of the changes
//                    stream.setAppliedLSN(stream.getLastReceiveLSN());
//                    stream.setFlushedLSN(stream.getLastReceiveLSN());
//                } else {
//                    TimeUnit.MILLISECONDS.sleep(10);
//                }
//            }
//        } catch (SQLException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        String url = "jdbc:postgresql://localhost:5432/pgexrep";
        Properties props = new Properties();
        PGProperty.USER.set(props, "postgres");
        PGProperty.PASSWORD.set(props, "postgres");
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
        PGProperty.REPLICATION.set(props, "database");
        PGProperty.PREFER_QUERY_MODE.set(props, "simple");

        Connection con = DriverManager.getConnection(url, props);
        PGConnection replConnection = con.unwrap(PGConnection.class);

        PGReplicationStream stream =
                replConnection.getReplicationAPI()
                        .replicationStream()
                        .logical()
                        .withSlotName("pgexrep_public")
                        .withSlotOption("proto_version", 1)
                        .withSlotOption("publication_names", "pgexrep_public")
                        .withStatusInterval(20, TimeUnit.SECONDS)
                        .start();

        while (true) {
            //non blocking receive message
            ByteBuffer msg = stream.readPending();

            if (msg == null) {
                TimeUnit.MILLISECONDS.sleep(10L);
                continue;
            }
            //PgOutputMessageDecoder

            //decodeLogicalReplicationMessage(msg);
            System.out.println(new PgOutput(msg).toString());

            //feedback
            stream.setAppliedLSN(stream.getLastReceiveLSN());
            stream.setFlushedLSN(stream.getLastReceiveLSN());
        }

    }

    private String decodeLogicalReplicationMessage(ByteBuffer buffer) {
        // Позиционируем буфер в начало
        buffer.position(0);

        // Чтение типа сообщения
        char messageType = (char) buffer.get();

        // Чтение длины сообщения
        int messageLength = buffer.getInt();

        // Декодирование в строку
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return new String(data, StandardCharsets.UTF_8);
    }
}
