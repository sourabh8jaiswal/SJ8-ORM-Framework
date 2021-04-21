package com.sourabh.jaiswal.school;
import orm.sj.annotations.*;
@Table(name="studentview")
public class Studentview
{
@Column(name="roll_number")
public int rollNumber;
@Column(name="first_name")
public String firstName;
@Column(name="last_name")
public String lastName;
@Column(name="title")
public String title;
@Column(name="aadhar_card_number")
public String aadharCardNumber;
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
public void setTitle(String title)
{
this.title=title;
}
public String getTitle()
{
return this.title;
}
public void setAadharCardNumber(String aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
public String getAadharCardNumber()
{
return this.aadharCardNumber;
}
}
