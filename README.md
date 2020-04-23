# SimpleMessenger
This is a simple messenger which uses TCP sockets to communicate between two instances of the program.
One instance must be set to server mode and the second to client mode. Messenges can be encrypted with 
AES-128 cipher.

# Usage
- Switch your instance of the program to client mode (your partner must have set his instance to the server mode)
- Type in the IP of your partner in the TextField under "Connection" label
- Click on the "Connect" button
- (Optional) Select encryption type and choose a key (note that it should match the key of your partner exactly)
- Once connected, type in your messenge and click on the "Send" button

![Snapshot of the GUI](/GUI.png)
