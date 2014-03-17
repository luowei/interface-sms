Select Top 1 ID From GuestBook Where Name='" & UserName & "'
Select Top 1 user_id From expert.dbo.Et_User Where user_loginname='" & UserName & "'
Select count(1) from Guestbook_BandMobile where Band_Mobile = '" & UserName & "' and Band_Check=1




--产生验证码
Select * from pcdb.dbo.GuestBook_WapVerify Where [Verify_Mobile] = '" & UserName&"'

Insert [pcdb].[dbo].[GuestBook_WapVerify] (Verify_Mobile,Verify_ExpireTime,Verify_Code)
  Values('"&UserName&"',DATEADD ( Hour , 1, getdate() ),'"&TmpPwd&"')

Update [pcdb].[dbo].[GuestBook_WapVerify] Set Verify_ExpireTime=DATEADD ( Hour , 1, getdate() ) ,
  Verify_Code='"&TmpPwd&"' Where Verify_Mobile = '" & UserName&"'


--发送一条短信
Insert expert.dbo.Et_Sms (sms_phone,sms_message,sms_seq,sms_title,sms_time,sms_fromuserid,sms_touserid,sms_sendResult,sms_msg_Type,sms_Amount,sms_sendType,sms_OnlyID,sms_ip,sms_admin,sms_sendList_id,sms_AccountID,sms_NeedStatus,sms_Priority,sms_Retry,sms_loginname,sms_toWhoName)

  Values('13801808080','您在本站申请了注册验证码，确认码为：2942，隆众石化网http://www.oilchem.net',Null,'发送注册验证码'
         ,GetDate(),0,0,0,100,1,1,NULL,'192.168.1.76',NULL,NULL,'0',0,1,3,'系统','13801808080')

