package dto;
import java.sql.Timestamp;

public class UpdateHistoryDTO {
    private int logID;
    private String actionType;
    private String tableName;
    private int recordID;
    private String oldValue;
    private String newValue;
    private int performedBy;
    private Timestamp performedAt;

    public UpdateHistoryDTO(int logID, String actionType, String tableName, int recordID, String oldValue, String newValue, int performedBy, Timestamp performedAt) {
        this.logID = logID;
        this.actionType = actionType;
        this.tableName = tableName;
        this.recordID = recordID;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
    }
    // Getter & Setter
    public int getLogID() { return logID; }
    public void setLogID(int logID) { this.logID = logID; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public int getRecordID() { return recordID; }
    public void setRecordID(int recordID) { this.recordID = recordID; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public int getPerformedBy() { return performedBy; }
    public void setPerformedBy(int performedBy) { this.performedBy = performedBy; }
    public Timestamp getPerformedAt() { return performedAt; }
    public void setPerformedAt(Timestamp performedAt) { this.performedAt = performedAt; }
} 