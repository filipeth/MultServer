import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class ServidorSub {

	public static void main(String[] args) {
		ObjectOutputStream enviaMensagem;
		ObjectInputStream recebeMensagem;
		Scanner scanner = new Scanner(System.in);
		Socket socketCliente = null;
		String calc;
		float valores[] = new float[2];
		try {
			
			//INICIA CONEXAO COM O SERVIDOR PRINCIPAL
			System.out.println("Digite o ip do servidor principal: ");
			String ipServidor = scanner.nextLine();
			socketCliente = 	new Socket(ipServidor, 9999);
			System.out.println("SERVIDOR DE SUBTRACAO CONECTADO AO SERVIDOR PRINCIPAL DE IP: " + ipServidor);
			
			//INICIA VARIAVEIS
			recebeMensagem = new ObjectInputStream(socketCliente.getInputStream());
			enviaMensagem = new ObjectOutputStream(socketCliente.getOutputStream());
			
			while (socketCliente.isConnected()){
				calc = (String) recebeMensagem.readObject();
				System.out.println("SERVIDOR ENVIOU: " + calc);
				String str[] = calc.split("#");
				valores[0] = Float.valueOf(str[0]);
				valores[1] = Float.valueOf(str[1]);
				float resultado = valores[0]-valores[1];
				System.out.println("ENVIANDO AO SERVIDOR: " + resultado);
				enviaMensagem.writeObject(resultado);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				socketCliente.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
