package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.kafka.OrderProducer;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/kafka")
public class KafkaTestController {
    private final OrderProducer orderProducer;
    public KafkaTestController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }@PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        orderProducer.sendOrder(message);
        return "Message sent successfully";
    }
}