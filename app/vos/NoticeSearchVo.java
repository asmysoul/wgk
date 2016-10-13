package vos;

import java.util.Date;
import java.util.List;

import models.Notice.NoticeType;
import models.User.UserType;


public class NoticeSearchVo extends Page {
    public Long id;
    public NoticeType type;
    public List<UserType> roles;
    public String title;
    public Date createTimeStart;
    public Date createTimeEnd;
    public Boolean isDisplay;
}
