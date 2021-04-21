package orm.sj;
public class TableInfo
{
private MethodInfo addObject;
private MethodInfo updateObject;
private MethodInfo deleteObject;
private MethodInfo queryObject;
private MethodInfo getByPrimaryKeyObject;
public TableInfo()
{
this.addObject=null;
this.updateObject=null;
this.deleteObject=null;
this.queryObject=null;
this.getByPrimaryKeyObject=null;
}
public void setAddObject(MethodInfo addObject)
{
this.addObject=addObject;
}
public MethodInfo getAddObject()
{
return this.addObject;
}
public void setUpdateObject(MethodInfo updateObject)
{
this.updateObject=updateObject;
}
public MethodInfo getUpdateObject()
{
return this.updateObject;
}
public void setDeleteObject(MethodInfo deleteObject)
{
this.deleteObject=deleteObject;
}
public MethodInfo getDeleteObject()
{
return this.deleteObject;
}
public void setQueryObject(MethodInfo queryObject)
{
this.queryObject=queryObject;
}
public MethodInfo getQueryObject()
{
return this.queryObject;
}
public void setGetByPrimaryKeyObject(MethodInfo getByPrimaryKeyObject)
{
this.getByPrimaryKeyObject=getByPrimaryKeyObject;
}
public MethodInfo getGetByPrimaryKeyObject()
{
return this.getByPrimaryKeyObject;
}
}