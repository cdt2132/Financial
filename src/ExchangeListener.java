import java.io.File;

import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

/**
 * ExchangeListener Class - acts as exchange and listens for orders
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 */


public class ExchangeListener implements Runnable {

	/** listen for fix messages */
	public void run() {
		try {
			
			// Create new acceptor file
			File file = new File("./conf/acceptor.cfg");
			System.out.println(file.getAbsolutePath());
			
			// Instantiate exchange acceptor and start FIX Accepter/Exchange server
			SessionSettings settings = new SessionSettings("./conf/acceptor.cfg");
			ExchangeAcceptor acceptor = new ExchangeAcceptor();
			ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
			DefaultMessageFactory messageFactory = new DefaultMessageFactory();
			FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);
			SocketAcceptor socketAcceptor = new SocketAcceptor(acceptor, fileStoreFactory, settings, screenLogFactory,
					messageFactory);
			socketAcceptor.start();
			
			// Stop exchange with keystroke
			System.out.println("press any key to stop the FIX Acceptor/ Exchange Server----->>>>>");
			System.in.read();
			socketAcceptor.stop();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
