package com.sourabh.jaiswal.school;
import orm.sj.annotations.*;
@Table(name="student")
public class Student
{
@PrimaryKey
@Column(name="roll_number")
public int rollNumber;
@Column(name="first_name")
public String firstName;
@Column(name="last_name")
public String lastName;
@Column(name="aadhar_card_number")
public String aadharCardNumber;
@ForeignKey(parent="course",column="code")
@Column(name="course_code")
public int courseCode;
@Column(name="gender")
public String gender;
@Column(name="date_of_birth")
public java.sql.Date dateOfBirth;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
public void setFirstName(String firstName)
{
this.firstName=firstName;
}
public String getFirstName()
{
return this.firstName;
}
public void setLastName(String lastName)
{
this.lastName=lastName;
}
public String getLastName()
{
return this.lastName;
}
public void setAadharCardNumber(String aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
public String getAadharCardNumber()
{
return this.aadharCardNumber;
}
public void setCourseCode(int courseCode)
{
this.courseCode=courseCode;
}
public int getCourseCode()
{
return this.courseCode;
}
public void setGender(String gender)
{
this.gender=gender;
}
public String getGender()
{
return this.gender;
}
public void setDateOfBirth(java.sql.Date dateOfBirth)
{
this.dateOfBirth=dateOfBirth;
}
public java.sql.Date getDateOfBirth()
{
return this.dateOfBirth;
}
}
