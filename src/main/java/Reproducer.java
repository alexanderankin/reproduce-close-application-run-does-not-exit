import lombok.SneakyThrows;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Reproducer {
    @SneakyThrows
    public static void main(String[] args) {
        String rootDir = Arrays.stream(args).findAny()
                .orElse(System.getProperty("user.dir"));

        Path rootDirPath = Path.of(rootDir);
        System.out.println("started in " + rootDirPath);

        new ProcessBuilder("find", ".", "-name", "*jar").inheritIO().start().waitFor();

        Path springJar = rootDirPath.resolve(Path.of("./spring-example", "build", "libs", "spring-example.jar"));
        Path micronautJar = rootDirPath.resolve(Path.of("./micronaut-example", "build", "libs", "micronaut-example-0.1-all.jar"));

        Duration expected = Duration.ofSeconds(10);

        long ts0 = System.nanoTime();
        Process spring = new ProcessBuilder("java", "-jar", springJar.toString()).inheritIO().start();
        CompletableFuture<Integer> springExit = exit(spring::waitFor);
        Integer springExitCode = springExit.get(expected.toMillis(), MILLISECONDS);
        long ts1 = System.nanoTime();
        System.out.println("spring exited with: " + springExitCode);
        System.out.println("spring took: " + Duration.ofNanos(ts1 - ts0).toString());

        Process micronaut = new ProcessBuilder("java", "-jar", micronautJar.toString()).inheritIO().start();
        // CompletableFuture<Integer> micronautExit = exit(micronaut::waitFor);
        try {
            micronaut.waitFor(expected.toMillis(), MILLISECONDS);
            Integer micronautExitCode = micronaut.exitValue();
            System.out.println("micronaut exited with: " + micronautExitCode);
        } catch (Exception e) {
            System.out.println("micronaut exit: " + e.getMessage());
            e.printStackTrace();
            System.out.println("micronaut did not exit in the expected time frame: " + expected);
            // need to manually exit
            micronaut.destroy();
        }
    }

    private static CompletableFuture<Integer> exit(Callable<Integer> callable) {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        new Thread(() -> {
            try {
                cf.complete(callable.call());
            } catch (Exception e) {
                cf.completeExceptionally(e);
            }
        }).start();
        return cf;
    }
}
