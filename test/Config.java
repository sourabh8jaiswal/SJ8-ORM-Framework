public class Config implements java.io.Serializable
{
private String jdbcDriver;
private String connectionUrl;
private String username;
private String password;
public Config()
{
this.jdbcDriver="";
this.connectionUrl="";
this.username="";
this.password="";
}
public void setJdbcDriver(String jdbcDriver)
{
this.jdbcDriver=jdbcDriver;
}
public String getJdbcDriver()
{
return this.jdbcDriver;
}
public void setConnectionUrl(String connectionUrl)
{
this.connectionUrl=connectionUrl;
}
public String getConnectionUrl()
{
return this.connectionUrl;
}
public void setUsername(String username)
{
this.username=username;
}
public String getUsername()
{
return this.username;
}
public void setPassword(String password)
{
this.password=password;
}
public String getPassword()
{
return this.password;
}
}