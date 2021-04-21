import orm.sj.*;
import java.util.*;
import java.text.*;
import com.sourabh.jaiswal.school.*;
import orm.sj.annotations.*;
public class TestStudentQuery
{
public static void main(String gg[]) throws Exception
{
DataManager dm=DataManager.getDataManager();
List<Object> lst=null;
try
{
dm.begin();
lst=dm.query(Student.class);
dm.end();
}catch(DataException e)
{
dm.end();
System.out.println(e);
}
for(Object o: lst)
{
Student s=(Student)o;
System.out.println(s.getRollNumber());
System.out.println(s.getFirstName());
System.out.println(s.getLastName());
System.out.println(s.getCourseCode());
System.out.println(s.getDateOfBirth());
System.out.println(s.getAadharCardNumber());
System.out.println(s.getGender());
}
}
}