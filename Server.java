import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Server {

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(59898, 512, InetAddress.getByName("192.168.50.133"))) {
            System.out.println("The remote procedure call server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new RemoteProcedureCall(listener.accept()));
            }
        }
    }

    private static class RemoteProcedureCall implements Runnable {
        private Socket socket;

        RemoteProcedureCall(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) {
                    /* Call the procedure and return the result */
                    String procedureResult = callProcedure(in.nextLine());
                    /* Send the result to the socket */
                    out.println(procedureResult);
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                System.out.println("Closed: " + socket);
            }
        }
    }

    public static String callProcedure(String rawString)
    {
        /* Split the raw string into the procedure name and its arguments */
        String[] args = rawString.split(",");
        String procedureName = args[0];

        /* Evaluate the result based on the procedure and its arguments */
        String result = switch (procedureName) {
            case "add" -> add(args[1], args[2]);
            case "tre" -> tre(args[1], args[2], args[3]);
            case "sub" -> sub(args[1], args[2]);
            case "mul" -> mul(args[1], args[2]);
            case "div" -> div(args[1], args[2]);
            case "rem" -> rem(args[1], args[2]);
            default -> "no match";
        };

        /* Notify the console which procedure was called and with what arguments */
        StringBuilder evaluationMessage = new StringBuilder();
        evaluationMessage.append("Evaluated ").append(procedureName).append("(");
        for (int i = 1; i < args.length - 1; i++)
            evaluationMessage.append(args[i]).append(",");
        evaluationMessage.append(args[args.length - 1]).append(") to ").append(result);
        System.out.println(evaluationMessage);

        /* Return the result as a string */
        return result;
    }

    public static String add(String num1, String num2)
    {
        int result = Integer.parseInt(num1) + Integer.parseInt(num2);
        return Integer.toString(result);
    }

    public static String tre(String num1, String num2, String num3)
    {
        int result = Integer.parseInt(num1) + Integer.parseInt(num2) + Integer.parseInt(num3);
        return Integer.toString(result);
    }

    public static String sub(String num1, String num2)
    {
        int result = Integer.parseInt(num1) - Integer.parseInt(num2);
        return Integer.toString(result);
    }

    public static String mul(String num1, String num2)
    {
        int result = Integer.parseInt(num1) * Integer.parseInt(num2);
        return Integer.toString(result);
    }

    public static String div(String num1, String num2)
    {
        int result = Integer.parseInt(num1) / Integer.parseInt(num2);
        return Integer.toString(result);
    }

    public static String rem(String num1, String num2)
    {
        int result = Integer.parseInt(num1) % Integer.parseInt(num2);
        return Integer.toString(result);
    }

}
