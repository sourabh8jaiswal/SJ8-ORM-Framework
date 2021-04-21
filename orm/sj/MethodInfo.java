package orm.sj;
import java.lang.reflect.*;
public class MethodInfo
{
private String methodName;
private String preparedStatement;
private Method[] getters;
private Method[] setters;
private String[] columnNames;
private Method[] objectSetters;
private Method[] resultSetGetters;
public MethodInfo()
{
this.methodName="";
this.preparedStatement="";
this.getters=null;
this.setters=null;
this.columnNames=null;
this.objectSetters=null;
this.resultSetGetters=null;
}
public void setMethodName(String methodName)
{
this.methodName=methodName;
}
public String getMethodName()
{
return this.methodName;
}
public void setPreparedStatement(String preparedStatement)
{
this.preparedStatement=preparedStatement;
}
public String getPreparedStatement()
{
return this.preparedStatement;
}
public void setSetters(Method[] setters)
{
this.setters=setters;
}
public Method[] getSetters()
{
return this.setters;
}
public void setGetters(Method[] getters)
{
this.getters=getters;
}
public Method[] getGetters()
{
return this.getters;
}
public void setColumnNames(String []columnNames)
{
this.columnNames=columnNames;
}
public String[] getColumnNames()
{
return this.columnNames;
}
public void setObjectSetters(Method[] objectSetters)
{
this.objectSetters=objectSetters;
}
public Method[] getObjectSetters()
{
return this.objectSetters;
}
public void setResultSetGetters(Method[] resultSetGetters)
{
this.resultSetGetters=resultSetGetters;
}
public Method[] getResultSetGetters()
{
return this.resultSetGetters;
}
}