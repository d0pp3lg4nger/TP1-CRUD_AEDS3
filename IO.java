import java.io.File;
import java.time.LocalDate;
import aed3.*;

public class IO {


    public static void main(String[] args) {

        (new File(".\\dados\\pessoas.db")).delete();
        (new File(".\\dados\\pessoas.hash_d.db")).delete();
        (new File(".\\dados\\pessoas.hash_c.db")).delete();
        (new File(".\\dados\\pets.db")).delete();
        (new File(".\\dados\\pets.hash_d.db")).delete();
        (new File(".\\dados\\pets.hash_c.db")).delete();

        Pessoa p1 = new Pessoa( "João", 4329.34F, LocalDate.of(1998, 11, 15));
        Pessoa p2 = new Pessoa("Ana", 5127.15F, LocalDate.of(1999, 4, 2));
        Pessoa p3 = new Pessoa("Carlos", 3987.86F, LocalDate.of(2001, 5, 23));
        Pessoa p;

        Pet a1 = new Pet("Totó", LocalDate.of(2021, 10, 3), 2);
        Pet a2 = new Pet("Naná", LocalDate.of(2019, 3, 30), 3);
        Pet a;

        try {

            Arquivo<Pessoa> arqPessoas = new Arquivo<>(Pessoa.class.getConstructor(), "pessoas");
            Arquivo<Pet> arqPets = new Arquivo<>(Pet.class.getConstructor(), "pets");
            
            // Criação dos objetos
            arqPessoas.create(p1);
            arqPessoas.create(p2);
            arqPessoas.create(p3);

            arqPets.create(a1);
            arqPets.create(a2);

            // Leitura dos objetos
            p = arqPessoas.read(1);
            if(p!=null)
                System.out.println(p);
            else
                System.out.println("Pessoa não encontrada!");

            a = arqPets.read(2);
            if(a!=null)
                System.out.println(a);
            else
                System.out.println("Pet não encontrado!");

            // Exclusão de pessoa
            if(arqPessoas.delete(1))
                System.out.println("\nPessoa 1 excluída!");
            else
                System.out.println("Não foi possível excluir a pessoa 1!");
            p = arqPessoas.read(1);
            if(p!=null)
                System.out.println(p);
            else
                System.out.println("Pessoa 1 não encontrada!");
            p = arqPessoas.read(2);
            if(p!=null)
                System.out.println(p);
            else
                System.out.println("Pessoa 2 não encontrada!");


            a2.nome = "Nanazinha";
            arqPets.update(a2);
            a2.nome = "Nana";
            arqPets.update(a2);
            a = arqPets.read(2);
            if(a!=null)
                System.out.println(a);
            else
                System.out.println("Pet não encontrado!");



            // Reorganização
            System.out.println("\n\nReorganização do arquivo de pessoas");
            arqPessoas.reorganizar();
            p = arqPessoas.read(1);
            if(p!=null)
                System.out.println(p);
            else
                System.out.println("Pessoa 1 não encontrada!");
            p = arqPessoas.read(2);
            if(p!=null)
                System.out.println(p);
            else
                System.out.println("Pessoa 2 não encontrada!");
            p = arqPessoas.read(3);
            if(p!=null)
                System.out.println(p);
            else
                System.out.println("Pessoa 3 não encontrada!");

        } catch(Exception e) {
            e.printStackTrace();;
        }

    }
}