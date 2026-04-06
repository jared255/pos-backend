import com.fastfoodpos.domain.model.Product;
import com.fastfoodpos.domain.port.in.ManageProductPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")
public class DevRunner implements CommandLineRunner {
    private final ManageProductPort productPort;
    public DevRunner(ManageProductPort productPort){
        this.productPort = productPort;
    }

    @Override
    public void run(String... args)
    {
        System.out.println("=== INICIANDO PRUEBAS BACKEND ===");
        // Create
        Product product = new Product(
                "Hamburguesa Test",
                "Producto de prueba",
                new BigDecimal("10.50"),
                10,
                Product.ProductStatus.ACTIVO
        );
    }
}