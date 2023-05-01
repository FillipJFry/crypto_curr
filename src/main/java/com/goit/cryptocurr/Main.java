package com.goit.cryptocurr;

import com.goit.cryptocurr.providers.FileDataProvider;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {

  public static void main(String[] args) throws Exception {
    FileDataProvider provider = new FileDataProvider(getResourcesRoot());
    Advisor advisor = new Advisor(provider);
    Optional<ICryptoCurrency> minCurr = advisor.pickByMinPrice();
    System.out.println(minCurr.get().getName());
    System.out.println(minCurr.get().min());
    System.out.println(minCurr.get().max());
//			assertFalse(minCurr.isEmpty());
//			assertEquals("DOGE", minCurr.get().getName());
//			assertEquals(new BigDecimal("0.129"), minCurr.get().min());
  }

  public static Path getTestResourcesRoot() {
    return getResourcesRoot(Main.class, "/");
  }

  public static Path getResourcesRoot() {
    return getResourcesRoot(Advisor.class, "/prices");
  }

  private static <T> Path getResourcesRoot(Class<T> cl, String resBasicPath) {
    URL url = cl.getResource(resBasicPath);
    try {
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
