package CulinaryAPI_app.publishers;

import CulinaryAPI_app.dtos.UserEventDto;
import CulinaryAPI_app.enums.ActionType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeliverymanEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public DeliverymanEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${Culinary.broker.exchange.deliverymanEventExchange}")
    private String exchangeDeliverymanEvent;


    public void publishDeliverymanEvent(UserEventDto userEventDto, ActionType actionType) {
        userEventDto.setActionType(actionType.toString());
        rabbitTemplate.convertAndSend(exchangeDeliverymanEvent, "deliveryman.service.event", userEventDto);
    }

}
