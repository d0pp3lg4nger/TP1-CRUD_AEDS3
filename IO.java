import java.io.File;
import java.time.LocalDate;
import aed3.*;

public class IO {


    public static void main(String[] args) {

        (new File(".\\dados\\tarefas.db")).delete();
        (new File(".\\dados\\tarefas.hash_d.db")).delete();
        (new File(".\\dados\\tarefas.hash_c.db")).delete();

        Tarefa t1 = new Tarefa("Estudar AEDS3", LocalDate.of(2021, 10, 3), LocalDate.of(2021, 10, 10), (byte) 0, (byte) 0);
        Tarefa t2 = new Tarefa("Cacar sapo", LocalDate.of(2021, 10, 3), LocalDate.of(2021, 10, 10), (byte) 1, (byte) 1);
        Tarefa t3 = new Tarefa("Lutar com um morcego", LocalDate.of(2021, 10, 3), LocalDate.of(2021, 10, 10), (byte) 2, (byte) 2);
        Tarefa t;

        try {

            Arquivo<Tarefa> arqTarefas = new Arquivo<>(Tarefa.class.getConstructor(), "tarefas");
            
            // Criação dos objetos
            arqTarefas.create(t1);
            arqTarefas.create(t2);
            arqTarefas.create(t3);

            // Leitura dos objetos
            t = arqTarefas.read(1);
            if(t!=null)
                System.out.println(t);
            else
                System.out.println("Tarefa não encontrada!");

            // Exclusão de Tarefa
            if(arqTarefas.delete(1))
                System.out.println("\nTarefa 1 excluída!");
            else
                System.out.println("Não foi possível excluir a Tarefa 1!");
            t = arqTarefas.read(1);
            if(t!=null)
                System.out.println(t);
            else
                System.out.println("Tarefa 1 não encontrada!");
            t = arqTarefas.read(2);
            if(t!=null)
                System.out.println(t);
            else
                System.out.println("Tarefa 2 não encontrada!");

            // Reorganização
            System.out.println("\n\nReorganização do arquivo de Tarefas");
            arqTarefas.reorganizar();
            t = arqTarefas.read(1);
            if(t!=null)
                System.out.println(t);
            else
                System.out.println("Tarefa 1 não encontrada!");
            t = arqTarefas.read(2);
            if(t!=null)
                System.out.println(t);
            else
                System.out.println("Tarefa 2 não encontrada!");
            t = arqTarefas.read(3);
            if(t!=null)
                System.out.println(t);
            else
                System.out.println("Tarefa 3 não encontrada!");

        } catch(Exception e) {
            e.printStackTrace();;
        }

    }
}