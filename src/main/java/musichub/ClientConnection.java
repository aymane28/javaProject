package musichub;

import musichub.net.Client;

public class ClientConnection
{
	public static void main (String[] args)
	{
		Client client = new Client(6666, "Test");
		client.connect("127.0.0.1");
	}
}