import java.io.File;

import quickfix.DefaultMessageFactory;
import quickfix.FileStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;

/**
 * Created by kunaljasty on 11/3/15.
 */
public class ExchangeListener implements Runnable {

	/** Code to listen for FIX messages does here */
	public void run() {
		try {
			File file = new File("./conf/acceptor.cfg");
			System.out.println(file.getAbsolutePath());
			SessionSettings settings = new SessionSettings("./conf/acceptor.cfg");
			ExchangeAcceptor acceptor = new ExchangeAcceptor();
			ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
			DefaultMessageFactory messageFactory = new DefaultMessageFactory();
			FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);
			SocketAcceptor socketAcceptor = new SocketAcceptor(acceptor, fileStoreFactory, settings, screenLogFactory,
					messageFactory);
			socketAcceptor.start();
			System.out.println("press any key to stop the FIX Acceptor/ Exchange Server----->>>>>");
			System.in.read();
			socketAcceptor.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
