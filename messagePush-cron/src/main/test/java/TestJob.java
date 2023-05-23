import com.xxl.job.core.handler.annotation.XxlJob;
import org.junit.Test;
import org.springframework.stereotype.Component;

@Component
public class TestJob {

    @XxlJob("test")
    public void testExecute(){
        System.out.printf("success!");
    }
}
