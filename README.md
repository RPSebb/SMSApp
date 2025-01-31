Basic sms application.

Features : 
  - send sms
  - receive sms
  - notify when sms is receive

Lacking features :
  - mark sms as read
  - create new conversation
  - inform user of spam / scam phone number's
  - blacklist phone number
  - call

The application use default sms app from android as base.

It retrieve conversation from Telephony.Threads

And message from Telephony.Sms

Receive functionnality is done with a BroadcastReceiver :
  - When sms is receive, get current time
  - Wait 5 secondes for insertion in database
  - Try to retrieve the sms from database
  - Then try to retrieve the conversation base on thread_id

There is 2 screens :

  - HomeScreen where all conversations are listed

![HomeScreen](https://github.com/user-attachments/assets/a0ec6d8e-94d2-4a61-9912-4814926208fa)
  

  
  - ConversationScreen where all messages from a conversation are listed, and where message can be send

![ConversationScreen](https://github.com/user-attachments/assets/6e1a7899-3da5-471d-b37e-b1a76224af7f)
