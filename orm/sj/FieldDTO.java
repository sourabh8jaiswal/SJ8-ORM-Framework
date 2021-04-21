package orm.sj;
public class FieldDTO
{
private String fieldName;
private String columnName;
private String typeName;
private boolean isAutoIncrement;
private boolean isPrimaryKey;
private boolean isForeignKey;
private String fkParentName;
private String fkColumnName;
public FieldDTO()
{
this.fieldName="";
this.columnName="";
this.typeName="";
this.isAutoIncrement=false;
this.isPrimaryKey=false;
this.isForeignKey=false;
this.fkParentName="";
this.fkColumnName="";
}
public void setFieldName(String fieldName)
{
this.fieldName=fieldName;
}
public String getFieldName()
{
return this.fieldName;
}
public void setColumnName(String columnName)
{
this.columnName=columnName;
}
public String getColumnName()
{
return this.columnName;
}
public void setTypeName(String typeName)
{
this.typeName=typeName;
}
public String getTypeName()
{
return this.typeName;
}
public void isAutoIncrement(boolean isAutoIncrement)
{
this.isAutoIncrement=isAutoIncrement;
}
public boolean isAutoIncrement()
{
return this.isAutoIncrement;
}
public void isPrimaryKey(boolean isPrimaryKey)
{
this.isPrimaryKey=isPrimaryKey;
}
public boolean isPrimaryKey()
{
return this.isPrimaryKey;
}
public void isForeignKey(boolean isForeignKey)
{
this.isForeignKey=isForeignKey;
}
public boolean isForeignKey()
{
return this.isForeignKey;
}
public void setFkParentName(String fkParentName)
{
this.fkParentName=fkParentName;
}
public String getFkParentName()
{
return this.fkParentName;
}
public void setFkColumnName(String fkColumnName)
{
this.fkColumnName=fkColumnName;
}
public String getFkColumnName()
{
return this.fkColumnName;
}
}