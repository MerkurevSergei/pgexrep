package com.github.merkurevsergei.pgexrep.decoder.pgoutput;

import io.debezium.connector.postgresql.PostgresType;
import io.debezium.connector.postgresql.connection.pgoutput.ColumnMetaData;
import io.debezium.util.Strings;
import org.postgresql.replication.LogSequenceNumber;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Optional;

public class PgOutput {
    ByteBuffer buffer;
    long nanoTime = 0L;
    public PgOutput(ByteBuffer b){
        buffer = b;
    }
    public String toString() {

        byte cmd = buffer.get();
        switch (cmd) {
            case 'Y':
                System.out.println("TYPE:");
            case 'B':
                nanoTime = System.nanoTime();
                System.out.println("Begin Issued: " + nanoTime);
                LogSequenceNumber finalLSN = LogSequenceNumber.valueOf(buffer.getLong());
                Timestamp commitTime = new Timestamp(buffer.getLong());
                int transactionId = buffer.getInt();
                return "BEGIN final LSN: " + finalLSN.toString() + " Commit Time: " + commitTime + " XID: " + transactionId;

            case 'C':
                // COMMIT
                System.out.println("Commit Issued: " + (System.nanoTime() -  nanoTime));
                byte unusedFlag = buffer.get();
                LogSequenceNumber commitLSN = LogSequenceNumber.valueOf( buffer.getLong() );
                LogSequenceNumber endLSN = LogSequenceNumber.valueOf( buffer.getLong() );
                commitTime = new Timestamp(buffer.getLong());
                return "COMMIT commit LSN:" + commitLSN.toString() + " end LSN:" + endLSN.toString() + " commitTime: " + commitTime;

            case 'U': // UPDATE
            case 'D': // DELETE
                StringBuffer sb = new StringBuffer(cmd=='U'?"UPDATE: ":"DELETE: ");
                int oid = buffer.getInt();
                /*
                 this can be O or K if Delete or possibly N if UPDATE
                 K means key
                 O means old data
                 N means new data
                 */
                char keyOrTuple = (char)buffer.get();
                getTuple(buffer, sb);
                return sb.toString();

            case 'I':
                sb = new StringBuffer("INSERT: ");
                // oid of relation that is being inserted
                oid = buffer.getInt();
                // should be an N
                char isNew = (char)buffer.get();
                getTuple(buffer, sb);
                return sb.toString();
            case 'R':
                System.out.println("RELATION:");
                // oid of relation that is being inserted
                LinkedHashMap<String, String> relationInfo = new LinkedHashMap<>();
                oid = buffer.getInt();
                String schemaName = readString(buffer);
                String tableName = readString(buffer);
                int replicaIdentityId = buffer.get();
                short columnCount = buffer.getShort();

                relationInfo.put("Oid", String.valueOf(oid));
                relationInfo.put("schemaName", schemaName);
                relationInfo.put("tableName", tableName);
                relationInfo.put("replicaIdentityId", String.valueOf(replicaIdentityId));
                relationInfo.put("columnCount", String.valueOf(columnCount));

                for (short i = 0; i < columnCount; ++i) {
                    byte flags = buffer.get();
                    String columnName = Strings.unquoteIdentifierPart(readString(buffer));
                    int columnType = buffer.getInt();
                    int attypmod = buffer.getInt();

                    LinkedHashMap<String, String> column = new LinkedHashMap<>();
                    column.put("flags", String.valueOf(flags));
                    column.put("columnType", String.valueOf(columnType));
                    column.put("attypmod", String.valueOf(attypmod));
                    relationInfo.put(columnName, column.toString());
                }
                return relationInfo.toString();
        }
        return "";
    }

    private void getTuple(ByteBuffer buffer, StringBuffer sb) {
        short numAttrs;
        numAttrs = buffer.getShort();
        for (int i = 0; i < numAttrs; i++) {
            byte c = buffer.get();
            switch (c) {
                case 'n': // null
                    sb.append("NULL, ");
                    break;
                case 'u': // unchanged toast column
                    break;
                case 't': // textual data
                    int strLen = buffer.getInt();
                    byte[] bytes = new byte[strLen];
                    buffer.get(bytes, 0, strLen);
                    String value = new String(bytes);
                    sb.append(value).append(", ");
                    break;
                default:
                    sb.append("command: ").append((char) c);

            }
        }
    }
    private  String getString(ByteBuffer buffer){
        StringBuffer sb = new StringBuffer();
        while ( true ){
            byte c = buffer.get();
            if ( c == 0 ) {
                break;
            }
            sb.append((char)c);
        }
        return sb.toString();
    }

    /**
     * Reads the replication stream up to the next null-terminator byte and returns the contents as a string.
     *
     * @param buffer The replication stream buffer
     * @return string read from the replication stream
     */
    private static String readString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        byte b = 0;
        while ((b = buffer.get()) != 0) {
            sb.append((char) b);
        }
        return sb.toString();
    }
}