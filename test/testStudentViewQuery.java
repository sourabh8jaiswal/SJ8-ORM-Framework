import java.util.*;
import java.text.*;
import com.sourabh.jaiswal.school.*;
import orm.sj.annotations.*;
public class TestStudentViewQuery
{
public static void main(String gg[]) throws Exception
{
DataManager dm=DataManager.getDataManager();
List<Object> lst=null;
try
{
dm.begin();
lst=dm.query(Studentview.class);
dm.end();
}catch(DataException e)
{
dm.end();
System.out.println(e);
}
for(Object o: lst)
{
Studentview s=(Studentview)o;
System.out.println(s.getRollNumber());
System.out.println(s.getFirstName());
System.out.println(s.getLastName());
System.out.println(s.getTitle());
System.out.println(s.getAadharCardNumber());
System.out.println("************************************************************");
}
}
}