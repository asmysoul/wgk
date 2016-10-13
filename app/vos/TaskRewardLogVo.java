package vos;

import java.util.Date;
import java.util.List;

import models.Notice.NoticeType;
import models.User.UserType;


public class TaskRewardLogVo extends Page {
    public Long id;
    public long inviteUserId;
    public String inviteUserNick;
    public long userId;
    public String userNick;
    public Date taskFinishedTimeStart;
    public Date taskFinishedTimeEnd;
}
