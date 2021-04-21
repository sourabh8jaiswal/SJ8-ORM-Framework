package com.sourabh.jaiswal.school;
import orm.sj.annotations.*;
@Table(name="course")
public class Course
{
@ExportedKey(parent="student",column="course_code")
@PrimaryKey
@AutoIncrement
@Column(name="code")
public int code;
@Column(name="title")
public String title;
public void setCode(int code)
{
this.code=code;
}
public int getCode()
{
return this.code;
}
public void setTitle(String title)
{
this.title=title;
}
public String getTitle()
{
return this.title;
}
}
