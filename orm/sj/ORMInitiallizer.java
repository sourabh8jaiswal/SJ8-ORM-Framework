package orm.sj;
import java.io.*;
import java.sql.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;
public class ORMInitiallizer
{
public static String snakeToCamelCase(String name)
{
String str[]=name.split("_");
String fieldName=str[0];
int i=1;
while(i<str.length)
{
fieldName=fieldName+str[i].substring(0,1).toUpperCase()+str[i].substring(1);
i++;
}
return fieldName;
}
public static void main(String gg[]) throws Exception
{
JSONParser jsonParser=new JSONParser();
Object obj=jsonParser.parse(new FileReader("conf.json"));
JSONObject jsonObject=(JSONObject)obj;
String jdbcDriver=(String)jsonObject.get("jdbc-driver");
String connectionURL=(String)jsonObject.get("connection-url");
String username=(String)jsonObject.get("username");
String password=(String)jsonObject.get("password");
String packageName=(String)jsonObject.get("package-name");
String jarFileName=(String)jsonObject.get("jar-file-name");
String pth=packageName.replaceAll("\\.","\\\\");
File file1=new File(pth);
String absPath=file1.getAbsolutePath();
System.out.println(absPath);
//packageName=packageName;
File file2=new File(absPath);
System.out.println(jdbcDriver);
System.out.println(connectionURL);
System.out.println(username);
System.out.println(password);
System.out.println(pth);
System.out.println(jarFileName);
if(!file2.exists())
{
System.out.println(file2.mkdirs());
}
Class.forName(jdbcDriver);
Connection con=DriverManager.getConnection(connectionURL,username,password);
DatabaseMetaData dbmd=con.getMetaData();
ResultSet rs=dbmd.getTables(null,null,null,new String[]{"TABLE"});
System.out.println();
File file;
RandomAccessFile randomAccessFile;
while(rs.next())
{
String tableName=rs.getString("TABLE_NAME");
//System.out.println("Table name : "+tableName);
String className=String.valueOf(tableName.charAt(0)).toUpperCase()+tableName.substring(1);
file=new File(absPath+File.separator+className+".java");
randomAccessFile=new RandomAccessFile(file,"rw");
randomAccessFile.writeBytes("package "+packageName+";\r\n");
randomAccessFile.writeBytes("import orm.sj.annotations.*;\r\n");
randomAccessFile.writeBytes("@Table(name=\""+tableName+"\")\r\n");
randomAccessFile.writeBytes("public class "+className+"\r\n");
randomAccessFile.writeBytes("{\r\n");
ResultSet foreignKeys=dbmd.getImportedKeys(null,null,tableName);
String column=null;
String parent=null;
String fkColumn="";
if(foreignKeys.next())
{
fkColumn=foreignKeys.getString("FKCOLUMN_NAME");
System.out.println(fkColumn+", "+fkColumn.length());
column=foreignKeys.getString("PKCOLUMN_NAME");
System.out.println(column);
parent=foreignKeys.getString("PKTABLE_NAME");
System.out.println(parent);
foreignKeys.close();
}

ResultSet exportedKeys=dbmd.getExportedKeys(null,null,tableName);
String eKey="";
String eparent="";
String ecolumn="";
if(exportedKeys.next())
{
eKey=exportedKeys.getString("PKCOLUMN_NAME");
eparent=exportedKeys.getString("FKTABLE_NAME");
ecolumn=exportedKeys.getString("FKCOLUMN_NAME");
exportedKeys.close();
}

ResultSet primaryKeys=dbmd.getPrimaryKeys(null,null,tableName);
primaryKeys.next();
String primaryKey=primaryKeys.getString(4);
primaryKeys.close();
List<String> lst=new ArrayList<>();
ResultSet columns=dbmd.getColumns(null,null,tableName,null);
while(columns.next())
{
//System.out.println("Column position: "+columns.getString("ORDINAL_POSITION"));
String columnName=columns.getString("COLUMN_NAME");
System.out.println("Column name: "+columnName+","+columnName.length());
String typeName=columns.getString("TYPE_NAME").toLowerCase();
//System.out.println("Column type: "+typeName);
String isAutoIncrement=columns.getString("IS_AUTOINCREMENT");
if(typeName.equals("char")) typeName="String";
if(typeName.equals("date")) typeName="java.sql.Date";
System.out.println(fkColumn);
if(columnName.equals(eKey)) randomAccessFile.writeBytes("@ExportedKey(parent=\""+eparent+"\",column=\""+ecolumn+"\")\r\n");
if(columnName.equals(fkColumn)) randomAccessFile.writeBytes("@ForeignKey(parent=\""+parent+"\",column=\""+column+"\")\r\n");
if(columnName.equals(primaryKey)) randomAccessFile.writeBytes("@PrimaryKey\r\n");
if(isAutoIncrement.equals("YES")) randomAccessFile.writeBytes("@AutoIncrement\r\n");
randomAccessFile.writeBytes("@Column(name=\""+columnName+"\")\r\n");
String fieldName=snakeToCamelCase(columnName);
randomAccessFile.writeBytes("public "+typeName+" "+fieldName+";\r\n");
String setterName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
String getterName="get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
String ss="public void "+setterName+"("+typeName+" "+fieldName+")\r\n{\r\nthis."+fieldName+"="+fieldName+";\r\n}\r\n";
//System.out.println(ss);
String g="public "+typeName+" "+getterName+"()\r\n{\r\nreturn this."+fieldName+";\r\n}\r\n";
//System.out.println(g);
lst.add(ss+g);
}
columns.close();
for(String mm: lst)
{
randomAccessFile.writeBytes(mm);
}
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.close();
}
rs.close();


/* CODE TO CREATE CLASSES FOR VIEW */

rs=dbmd.getTables(null,null,null,new String[]{"VIEW"});
while(rs.next())
{
String viewName=rs.getString("TABLE_NAME");
//System.out.println("Table name : "+viewName);
String className=String.valueOf(viewName.charAt(0)).toUpperCase()+viewName.substring(1);
file=new File(absPath+File.separator+className+".java");
randomAccessFile=new RandomAccessFile(file,"rw");
randomAccessFile.writeBytes("package "+packageName+";\r\n");
randomAccessFile.writeBytes("import orm.sj.annotations.*;\r\n");
randomAccessFile.writeBytes("@Table(name=\""+viewName+"\")\r\n");
randomAccessFile.writeBytes("public class "+className+"\r\n");
randomAccessFile.writeBytes("{\r\n");

List<String> lst=new ArrayList<>();
ResultSet columns=dbmd.getColumns(null,null,viewName,null);
while(columns.next())
{
//System.out.println("Column position: "+columns.getString("ORDINAL_POSITION"));
String columnName=columns.getString("COLUMN_NAME");
System.out.println("Column name: "+columnName+","+columnName.length());
String typeName=columns.getString("TYPE_NAME").toLowerCase();
//System.out.println("Column type: "+typeName);
String isAutoIncrement=columns.getString("IS_AUTOINCREMENT");
if(typeName.equals("char")) typeName="String";
if(typeName.equals("date")) typeName="java.sql.Date";
if(isAutoIncrement.equals("YES")) randomAccessFile.writeBytes("@AutoIncrement\r\n");
randomAccessFile.writeBytes("@Column(name=\""+columnName+"\")\r\n");
String fieldName=snakeToCamelCase(columnName);
randomAccessFile.writeBytes("public "+typeName+" "+fieldName+";\r\n");
String setterName="set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
String getterName="get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
String ss="public void "+setterName+"("+typeName+" "+fieldName+")\r\n{\r\nthis."+fieldName+"="+fieldName+";\r\n}\r\n";
//System.out.println(ss);
String g="public "+typeName+" "+getterName+"()\r\n{\r\nreturn this."+fieldName+";\r\n}\r\n";
//System.out.println(g);
lst.add(ss+g);
}
columns.close();
for(String mm: lst)
{
randomAccessFile.writeBytes(mm);
}
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.close();
}
rs.close();

con.close();


/* here we will do the work of compiling generated classes and creating jar file */
File file4=new File("dist");
if(!file4.exists()) file4.mkdir();
File file3=new File("");

Runtime runtime=Runtime.getRuntime();
String app1="javac -cp "+file3.getAbsolutePath()+"\\sjannotations.jar;. "+pth+"\\*.java";
String app2="jar -cvf dist\\"+jarFileName+" "+pth;
System.out.println("App 1: "+app1);
System.out.println("App 2: "+app2);
try
{
Process p1=runtime.exec(app1);
while(p1.isAlive())
{
// do nothing
}
Process p2=runtime.exec(app2);
}catch(Exception e)
{
System.out.println(e);
}

}
}