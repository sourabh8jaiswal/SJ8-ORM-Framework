package orm.sj;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.json.simple.parser.*;
import org.json.simple.*;
import java.sql.*;
import orm.sj.annotations.*;
public class DataManager
{
private static HashMap<String,TableInfo> tablesMap=new HashMap<>();
private Connection connection;
private static DataManager dataManager=new DataManager();
private DataManager()
{
// do nothing
}
static
{
try
{
//Now we will read json file using json-simple-parser instead of google's gson

JSONParser jsonParser=new JSONParser();
Object oo=jsonParser.parse(new FileReader("conf.json"));
JSONObject jsonObject=(JSONObject)oo;
String connectionURL=(String)jsonObject.get("connection-url");
String username=(String)jsonObject.get("username");
String jdbcDriver=(String)jsonObject.get("jdbc-driver");
String password=(String)jsonObject.get("password");
String packageName=(String)jsonObject.get("package-name");

Class.forName(jdbcDriver);
Connection connection=DriverManager.getConnection(connectionURL,username,password);
DatabaseMetaData dbmd=connection.getMetaData();

ResultSet tablesResultSet=dbmd.getTables(null,null,null,new String[]{"TABLE"});
TableInfo tableInfo;
while(tablesResultSet.next())
{
tableInfo=new TableInfo();
String tableName=tablesResultSet.getString("TABLE_NAME");
String className=tableName.substring(0,1).toUpperCase()+tableName.substring(1);
Class cls=Class.forName(packageName+"."+className);
//System.out.println(className);
Field fields[]=cls.getDeclaredFields();
String primaryKeyFieldName=null;
String primaryKeyColumnName=null;
String primaryKeyType=null;
String foreignKeyFieldName=null;
String foreignKeyParentName=null;
String foreignKeyColumnName=null;
String autoIncrementFieldName=null;
String autoIncrementColumnName=null;
//HashMap<String,FieldDTO> propertiesMap=new HashMap<>();
List<FieldDTO> propertiesMap=new ArrayList<>();
for(Field field: fields)
{
String fieldName=field.getName();
String fieldType=field.getType().getName();
String columnName=null;
FieldDTO fieldDTO=new FieldDTO();
fieldDTO.setFieldName(fieldName);
fieldDTO.setTypeName(fieldType);

Annotation an=field.getAnnotation(Column.class);
if(an!=null)
{
Column column=(Column)an;
columnName=column.name();
fieldDTO.setColumnName(columnName);
}

an=field.getAnnotation(PrimaryKey.class);
if(an!=null)
{
primaryKeyFieldName=fieldName;
primaryKeyColumnName=columnName;
primaryKeyType=fieldType;
fieldDTO.isPrimaryKey(true);
}

an=field.getAnnotation(ForeignKey.class);
if(an!=null)
{
ForeignKey foreignKey=(ForeignKey)an;
foreignKeyFieldName=fieldName;
foreignKeyParentName=foreignKey.parent();
foreignKeyColumnName=foreignKey.column();
fieldDTO.isForeignKey(true);
fieldDTO.setFkParentName(foreignKeyParentName);
fieldDTO.setFkColumnName(foreignKeyColumnName);
}

an=field.getAnnotation(AutoIncrement.class);
if(an!=null)
{
autoIncrementFieldName=fieldName;
autoIncrementColumnName=columnName;
fieldDTO.isAutoIncrement(true);
}

propertiesMap.add(fieldDTO);
}

// here we will do the work of creating methods preparedStatement

MethodInfo addMethod=new MethodInfo();
String addPS="insert into "+tableName+" (";
int cnt=0;
List<String> types=new ArrayList<>();
List<String> fieldNames=new ArrayList<>();
for(FieldDTO f:propertiesMap)
{
if(!f.isAutoIncrement())
{
addPS=addPS+f.getColumnName()+",";
fieldNames.add(f.getFieldName());
types.add(f.getTypeName());
cnt++;
}
}

addPS=addPS.substring(0,addPS.length()-1);
addPS=addPS+") values(";
for(int i=0;i<cnt;i++) addPS=addPS+"?,";
addPS=addPS.substring(0,addPS.length()-1)+")";


/* code to generate setters and getters array */
Method getters[]=new Method[fieldNames.size()];
Method setters[]=new Method[types.size()];
Class psClass=PreparedStatement.class;
Class gClass=Class.forName(packageName+"."+className);
Class intClass=int.class;
Class shortClass=short.class;
Class longClass=long.class;
Class floatClass=float.class;
Class doubleClass=double.class;
Class booleanClass=boolean.class;
Class byteClass=byte.class;
Class stringClass=String.class;
Class booleanCls=Class.forName("java.lang.Boolean");
for(int i=0;i<types.size();i++)
{
String type=types.get(i);
String getterName="get"+fieldNames.get(i).substring(0,1).toUpperCase()+fieldNames.get(i).substring(1);
getters[i]=gClass.getDeclaredMethod(getterName,new Class[0]);
if(type.equals("int"))
{
setters[i]=psClass.getDeclaredMethod("setInt",new Class[]{intClass,intClass});
}
if(type.equals("java.lang.String")) 
{
setters[i]=psClass.getDeclaredMethod("setString",new Class[]{intClass,stringClass});
}
if(type.equals("java.util.Date"))
{
setters[i]=psClass.getDeclaredMethod("setDate",new Class[]{intClass,Class.forName("java.sql.Date")});
}
if(type.equals("java.sql.Date"))
{
setters[i]=psClass.getDeclaredMethod("setDate",new Class[]{intClass,Class.forName("java.sql.Date")});
}
if(type.equals("short"))
{
setters[i]=psClass.getDeclaredMethod("setShort",new Class[]{intClass,shortClass});
}
if(type.equals("long"))
{
setters[i]=psClass.getDeclaredMethod("setLong",new Class[]{intClass,longClass});
}
if(type.equals("float"))
{
setters[i]=psClass.getDeclaredMethod("setFloat",new Class[]{intClass,floatClass});
}
if(type.equals("double"))
{
setters[i]=psClass.getDeclaredMethod("setDouble",new Class[]{intClass,doubleClass});
}
if(type.equals("byte"))
{
setters[i]=psClass.getDeclaredMethod("setByte",new Class[]{intClass,byteClass});
}
if(type.equals("boolean"))
{
setters[i]=psClass.getDeclaredMethod("setBoolean",new Class[]{intClass,booleanCls});
}
}

addMethod.setPreparedStatement(addPS);
addMethod.setSetters(setters);
addMethod.setGetters(getters);
//System.out.println(addPS);
//System.out.println(setters.length);
//System.out.println(getters.length);
tableInfo.setAddObject(addMethod);


// here we will generate PS of Update method
fieldNames.clear();
types.clear();
String updatePS="update "+tableName+" set ";
cnt=0;
for(FieldDTO f:propertiesMap)
{
if(!f.isPrimaryKey())
{
updatePS=updatePS+f.getColumnName()+"=?,";
fieldNames.add(f.getFieldName());
types.add(f.getTypeName());
cnt++;
}
}
updatePS=updatePS.substring(0,updatePS.length()-1);
updatePS=updatePS+" where "+primaryKeyColumnName+"=?";

/* code to generate setters getters for update method */
Method updateGetters[]=new Method[fieldNames.size()+1];
Method updateSetters[]=new Method[types.size()+1];
int i;
for(i=0;i<types.size();i++)
{
String type=types.get(i);
String getterName="get"+fieldNames.get(i).substring(0,1).toUpperCase()+fieldNames.get(i).substring(1);
updateGetters[i]=gClass.getDeclaredMethod(getterName,new Class[0]);
if(type.equals("int"))
{
updateSetters[i]=psClass.getDeclaredMethod("setInt",new Class[]{intClass,intClass});
}
if(type.equals("java.lang.String")) 
{
updateSetters[i]=psClass.getDeclaredMethod("setString",new Class[]{intClass,stringClass});
}
if(type.equals("java.util.Date"))
{
updateSetters[i]=psClass.getDeclaredMethod("setDate",new Class[]{intClass,Class.forName("java.sql.Date")});
}
if(type.equals("java.sql.Date"))
{
updateSetters[i]=psClass.getDeclaredMethod("setDate",new Class[]{intClass,Class.forName("java.sql.Date")});
}
if(type.equals("short"))
{
updateSetters[i]=psClass.getDeclaredMethod("setShort",new Class[]{intClass,shortClass});
}
if(type.equals("long"))
{
updateSetters[i]=psClass.getDeclaredMethod("setLong",new Class[]{intClass,longClass});
}
if(type.equals("float"))
{
updateSetters[i]=psClass.getDeclaredMethod("setFloat",new Class[]{intClass,floatClass});
}
if(type.equals("double"))
{
updateSetters[i]=psClass.getDeclaredMethod("setDouble",new Class[]{intClass,doubleClass});
}
if(type.equals("byte"))
{
updateSetters[i]=psClass.getDeclaredMethod("setByte",new Class[]{intClass,byteClass});
}
if(type.equals("boolean"))
{
updateSetters[i]=psClass.getDeclaredMethod("setBoolean",new Class[]{intClass,booleanCls});
}
}

String g="get"+primaryKeyFieldName.substring(0,1).toUpperCase()+primaryKeyFieldName.substring(1);
updateGetters[i]=gClass.getDeclaredMethod(g,new Class[0]);
if(primaryKeyType.equals("int")) updateSetters[i]=psClass.getDeclaredMethod("setInt",new Class[]{intClass,intClass});
if(primaryKeyType.equals("java.lang.String")) updateSetters[i]=psClass.getDeclaredMethod("setString",new Class[]{intClass,stringClass});
//System.out.println(updateSetters.length);
//System.out.println(updateGetters.length);
MethodInfo updateMethod=new MethodInfo();
updateMethod.setPreparedStatement(updatePS);
updateMethod.setSetters(updateSetters);
updateMethod.setGetters(updateGetters);
//System.out.println(updatePS);
tableInfo.setUpdateObject(updateMethod);


/* creating prepared statement of delete method */
String deletePS="delete from "+tableName+" where "+primaryKeyColumnName+"=?";
Method deleteSetters[]=new Method[1];
Method deleteGetters[]=new Method[1];
if(primaryKeyType.equals("int")) deleteSetters[0]=psClass.getDeclaredMethod("setInt",new Class[]{intClass,intClass});
if(primaryKeyType.equals("java.lang.String")) deleteSetters[0]=psClass.getDeclaredMethod("setString",new Class[]{intClass,stringClass});
g="get"+primaryKeyFieldName.substring(0,1).toUpperCase()+primaryKeyFieldName.substring(1);
deleteGetters[0]=gClass.getDeclaredMethod(g,new Class[0]);
//System.out.println(deleteSetters.length);
//System.out.println(deleteGetters.length);
MethodInfo deleteMethod=new MethodInfo();
deleteMethod.setPreparedStatement(deletePS);
deleteMethod.setSetters(deleteSetters);
deleteMethod.setGetters(deleteGetters);
//System.out.println(deletePS);
tableInfo.setDeleteObject(deleteMethod);


/* creating preparedStatement for getByPrimaryKey method */
String getByPS="select * from "+tableName+" where "+primaryKeyColumnName+"=?";
Method getBySetters[]=new Method[1];
Method getByGetters[]=new Method[1];
if(primaryKeyType.equals("int")) getBySetters[0]=psClass.getDeclaredMethod("setInt",new Class[]{intClass,intClass});
if(primaryKeyType.equals("java.lang.String")) getBySetters[0]=psClass.getDeclaredMethod("setString",new Class[]{intClass,stringClass});
g="get"+primaryKeyFieldName.substring(0,1).toUpperCase()+primaryKeyFieldName.substring(1);
getByGetters[0]=gClass.getDeclaredMethod(g,new Class[0]);
//System.out.println(getBySetters.length);
//System.out.println(getByGetters.length);
MethodInfo getByPKMethod=new MethodInfo();
getByPKMethod.setPreparedStatement(getByPS);
getByPKMethod.setSetters(getBySetters);
getByPKMethod.setGetters(getByGetters);
//System.out.println(getByPS);
tableInfo.setGetByPrimaryKeyObject(getByPKMethod);


/* creating preparedStatement for query Method */
String queryPS="select * from "+tableName;
MethodInfo queryMethod=new MethodInfo();
queryMethod.setPreparedStatement(queryPS);
Method []querySetters=new Method[propertiesMap.size()];
Method []queryGetters=new Method[propertiesMap.size()];
String []columnNames=new String[propertiesMap.size()];
Class resultSetClass=ResultSet.class;
Class objClass=Class.forName(packageName+"."+className);
int j=0;
for(FieldDTO field:propertiesMap)
{
String columnName=field.getColumnName();
String fieldName=field.getFieldName();
String type=field.getTypeName();
String setterName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
String getterName="";
if(type.equals("int"))
{
getterName="getInt";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{int.class});
}
if(type.equals("java.lang.String"))
{
getterName="getString";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{String.class});
}
if(type.equals("short"))
{
getterName="getShort";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{short.class});
}
if(type.equals("long"))
{
getterName="getLong";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{long.class});
}
if(type.equals("float"))
{
getterName="getFloat";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{float.class});
}
if(type.equals("double"))
{
getterName="getDouble";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{double.class});
}
if(type.equals("char"))
{
getterName="getString";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{String.class});
}
if(type.equals("boolean"))
{
getterName="getBoolean";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{boolean.class});
}
if(type.equals("java.sql.Date"))
{
getterName="getDate";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{java.sql.Date.class});
}
if(type.equals("java.util.Date"))
{
getterName="getDate";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{java.sql.Date.class});
}
if(type.equals("byte"))
{
getterName="getByte";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{byte.class});
}
columnNames[j]=columnName;
queryGetters[j]=resultSetClass.getDeclaredMethod(getterName,new Class[]{String.class});

j++;
}
queryMethod.setSetters(querySetters);
queryMethod.setGetters(queryGetters);
queryMethod.setColumnNames(columnNames);
//System.out.println(queryPS);
tableInfo.setQueryObject(queryMethod);


/* some extra changes required for GetByPK method*/
getByPKMethod.setObjectSetters(querySetters);
getByPKMethod.setResultSetGetters(queryGetters);
getByPKMethod.setColumnNames(columnNames);


tablesMap.put(packageName+"."+className,tableInfo);
}


/* CODE TO populate DS (tablesMap) with View Objects */

ResultSet viewsResultSet=dbmd.getTables(null,null,null,new String[]{"VIEW"});
while(viewsResultSet.next())
{
tableInfo=new TableInfo();
String tableName=viewsResultSet.getString("TABLE_NAME");
String className=tableName.substring(0,1).toUpperCase()+tableName.substring(1);
Class cls=Class.forName(packageName+"."+className);
//System.out.println(className);
Field fields[]=cls.getDeclaredFields();

List<FieldDTO> propertiesMap=new ArrayList<>();
for(Field field: fields)
{
String fieldName=field.getName();
String fieldType=field.getType().getName();
String columnName=null;
FieldDTO fieldDTO=new FieldDTO();
fieldDTO.setFieldName(fieldName);
fieldDTO.setTypeName(fieldType);

Annotation an=field.getAnnotation(Column.class);
if(an!=null)
{
Column column=(Column)an;
columnName=column.name();
fieldDTO.setColumnName(columnName);
}


propertiesMap.add(fieldDTO);
}

/* creating preparedStatement for query Method */
String queryPS="select * from "+tableName;
MethodInfo queryMethod=new MethodInfo();
queryMethod.setPreparedStatement(queryPS);
Method []querySetters=new Method[propertiesMap.size()];
Method []queryGetters=new Method[propertiesMap.size()];
String []columnNames=new String[propertiesMap.size()];
Class resultSetClass=ResultSet.class;
Class objClass=Class.forName(packageName+"."+className);
int j=0;
for(FieldDTO field:propertiesMap)
{
String columnName=field.getColumnName();
String fieldName=field.getFieldName();
String type=field.getTypeName();
String setterName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
String getterName="";
if(type.equals("int"))
{
getterName="getInt";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{int.class});
}
if(type.equals("java.lang.String"))
{
getterName="getString";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{String.class});
}
if(type.equals("short"))
{
getterName="getShort";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{short.class});
}
if(type.equals("long"))
{
getterName="getLong";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{long.class});
}
if(type.equals("float"))
{
getterName="getFloat";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{float.class});
}
if(type.equals("double"))
{
getterName="getDouble";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{double.class});
}
if(type.equals("char"))
{
getterName="getString";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{String.class});
}
if(type.equals("boolean"))
{
getterName="getBoolean";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{boolean.class});
}
if(type.equals("java.sql.Date"))
{
getterName="getDate";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{java.sql.Date.class});
}
if(type.equals("java.util.Date"))
{
getterName="getDate";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{java.sql.Date.class});
}
if(type.equals("byte"))
{
getterName="getByte";
querySetters[j]=objClass.getDeclaredMethod(setterName,new Class[]{byte.class});
}
columnNames[j]=columnName;
queryGetters[j]=resultSetClass.getDeclaredMethod(getterName,new Class[]{String.class});

j++;
}
queryMethod.setSetters(querySetters);
queryMethod.setGetters(queryGetters);
queryMethod.setColumnNames(columnNames);
//System.out.println(queryPS);
tableInfo.setQueryObject(queryMethod);


tablesMap.put(packageName+"."+className,tableInfo);
}





}catch(Exception e)
{
System.out.println(e);
}
}

public static DataManager getDataManager()
{
return DataManager.dataManager;
}


public void begin() throws DataException
{
try
{
JSONParser jsonParser=new JSONParser();
Object oo=jsonParser.parse(new FileReader("conf.json"));
JSONObject jsonObject=(JSONObject)oo;
String connectionURL=(String)jsonObject.get("connection-url");
String username=(String)jsonObject.get("username");
String jdbcDriver=(String)jsonObject.get("jdbc-driver");
String password=(String)jsonObject.get("password");

Class.forName(jdbcDriver);
this.connection=DriverManager.getConnection(connectionURL,username,password);
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}

public void end() throws DataException
{
try
{
if(this.connection!=null) this.connection.close();
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}
public int save(Object object) throws DataException
{
Class cls=object.getClass();
String className=cls.getName();
TableInfo tableInfo=tablesMap.get(className);
MethodInfo addMethodInfo=tableInfo.getAddObject();
if(addMethodInfo==null) throw new DataException("Method not allowed");
String ps=addMethodInfo.getPreparedStatement();
Method setters[]=addMethodInfo.getSetters();
Method getters[]=addMethodInfo.getGetters();
try
{
PreparedStatement preparedStatement=this.connection.prepareStatement(ps,Statement.RETURN_GENERATED_KEYS);
for(int i=0;i<setters.length;i++)
{
setters[i].invoke(preparedStatement,i+1,getters[i].invoke(object));
}
preparedStatement.executeUpdate();
ResultSet resultSet=preparedStatement.getGeneratedKeys();
int code=0;
if(resultSet.next())
{
code=resultSet.getInt(1);
}
resultSet.close();
preparedStatement.close();
return code;
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}
public void update(Object object) throws DataException
{
Class cls=object.getClass();
String className=cls.getName();
TableInfo tableInfo=tablesMap.get(className);
MethodInfo updateMethodInfo=tableInfo.getUpdateObject();
if(updateMethodInfo==null) throw new DataException("Method not allowed");
String ps=updateMethodInfo.getPreparedStatement();
Method setters[]=updateMethodInfo.getSetters();
Method getters[]=updateMethodInfo.getGetters();
try
{
PreparedStatement preparedStatement=this.connection.prepareStatement(ps);
for(int i=0;i<setters.length;i++)
{
setters[i].invoke(preparedStatement,i+1,getters[i].invoke(object));
}
preparedStatement.executeUpdate();
preparedStatement.close();
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}
public void delete(Class c,Object val) throws DataException
{
Class cls=c;
String className=cls.getName();
TableInfo tableInfo=tablesMap.get(className);
MethodInfo deleteMethodInfo=tableInfo.getDeleteObject();
if(deleteMethodInfo==null) throw new DataException("Method not allowed");
String ps=deleteMethodInfo.getPreparedStatement();
Method setters[]=deleteMethodInfo.getSetters();
Method getters[]=deleteMethodInfo.getGetters();
try
{
PreparedStatement preparedStatement=this.connection.prepareStatement(ps);
for(int i=0;i<setters.length;i++)
{
setters[i].invoke(preparedStatement,i+1,val);
}
preparedStatement.executeUpdate();
preparedStatement.close();
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}
public List<Object> query(Class c) throws DataException
{
List<Object> lst=new ArrayList<>();
String className=c.getName();
TableInfo tableInfo=tablesMap.get(className);
MethodInfo methodInfo=tableInfo.getQueryObject();
if(methodInfo==null) throw new DataException("Method not allowed");
String ps=methodInfo.getPreparedStatement();
Method[] setters=methodInfo.getSetters();
Method[] getters=methodInfo.getGetters();
String[] columnNames=methodInfo.getColumnNames();
try
{
PreparedStatement preparedStatement=this.connection.prepareStatement(ps);
ResultSet resultSet=preparedStatement.executeQuery();
Object obj=null;
while(resultSet.next())
{
obj=c.newInstance();
for(int j=0;j<setters.length;j++)
{
setters[j].invoke(obj,getters[j].invoke(resultSet,columnNames[j]));
}
lst.add(obj);
}
resultSet.close();
preparedStatement.close();
return lst;
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}
public Object getByPrimaryKey(Class c,Object obj) throws DataException
{
String className=c.getName();
TableInfo tableInfo=tablesMap.get(className);
MethodInfo methodInfo=tableInfo.getGetByPrimaryKeyObject();
if(methodInfo==null) throw new DataException("Method not allowed");
Method[] setters=methodInfo.getSetters();
Method[] getters=methodInfo.getGetters();
Method[] resultSetGetters=methodInfo.getResultSetGetters();
Method[] objectSetters=methodInfo.getObjectSetters();
String[] columnNames=methodInfo.getColumnNames();
String ps=methodInfo.getPreparedStatement();
try
{
PreparedStatement preparedStatement=this.connection.prepareStatement(ps);
for(int i=0;i<setters.length;i++)
{
setters[i].invoke(preparedStatement,i+1,obj);
}
ResultSet resultSet=preparedStatement.executeQuery();
Object object=c.newInstance();
if(resultSet.next())
{
for(int i=0;i<resultSetGetters.length;i++)
{
objectSetters[i].invoke(object,resultSetGetters[i].invoke(resultSet,columnNames[i]));
}
}else
{
throw new Exception("Invalid Primary Key: "+obj);
}
return object;
}catch(Exception e)
{
throw new DataException(e.getMessage());
}
}
}