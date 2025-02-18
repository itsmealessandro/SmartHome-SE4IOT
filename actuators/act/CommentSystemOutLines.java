import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommentSystemOutLines {

  public static void main(String[] args) {
    try {
      // Ottieni la directory corrente
      Path currentDir = Paths.get(".");

      // Trova tutti i file .java nella directory corrente
      try (Stream<Path> filePaths = Files.walk(currentDir)) {
        List<Path> javaFiles = filePaths
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".java"))
            .collect(Collectors.toList());

        // Processa ogni file .java
        for (Path file : javaFiles) {
          commentSystemOut(file);
        }
      }

//       System.out.println("Operazione completata. Modificati tutti i file .java nella cartella corrente.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void commentSystemOut(Path file) {
    try {
      // Leggi tutte le righe del file
      List<String> lines = Files.readAllLines(file);
      boolean modified = false;

      // Modifica le righe che contengono System.out.println
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i).trim();
//         if (line.contains("System.out.println") && !line.startsWith("//")) {
          lines.set(i, "// " + lines.get(i)); // Aggiungi il commento
          modified = true;
        }
      }

      // Scrivi il file solo se è stato modificato
      if (modified) {
        Files.write(file, lines);
//         System.out.println("Modificato: " + file.toString());
      }
    } catch (IOException e) {
      System.err.println("Errore nel file: " + file.toString());
      e.printStackTrace();
    }
  }
}
