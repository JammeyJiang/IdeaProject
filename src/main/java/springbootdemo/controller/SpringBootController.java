package springbootdemo.controller;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Api(tags = "登录")
@Controller
public class SpringBootController {
    @ApiOperation("登录")
    @PostMapping(value = "/springBoot/say")
    public @ResponseBody String say(){
        return  "hello world!";
    }


    @ApiOperation("发送消息")
    @PostMapping(value = "/springBoot/Send")
    public @ResponseBody String Send(String Msg){
        return  "hello world!";
    }


    public  void SendMSg(String Msg){
        //创建连接工厂
        ConnectionFactory factory=new ConnectionFactory();
        //配置RabbitMQ的连接相关信息
        factory.setHost("192.168.40.130"); //ip
        factory.setPort(5672); //端口号
        factory.setUsername("root"); //用户名
        factory.setPassword("root"); //密码

        Connection connection=null; //定义连接
        Channel channel=null;//定义通道
        try {
            try {
                connection=factory.newConnection(); //获取连接
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            channel=connection.createChannel(); //获取通道
            /**
             * 声明一个队列
             * 参数1：队列名称取值 任意的
             * 参数2：是否为持久化队列
             * 参数3：是否排外，如果排外，则这个队列只允许一个消费者监听
             * 参数4：是否自动删除队列，如果为true表示当队列中没有消息，也没有消费者连接时，会自动删除这个队列
             * 参数5：队列的其他属性，通常设置为null即可
             * 注意：
             *      1) 声明队列时，这个队列名称如果存在，则放弃声明；如果不存在，则会声明一个新的队列
             *      2) 队列名可以任意设置，但是要与消息接收时的队列名一致
             *      3) 这行代码可有可无，但是一定要在发送消息前确认队列名已经存在于RabbitMQ中，否则会出现问题
             */
            channel.queueDeclare("myQueue",true,false,false,null);

            String message="RabbitMQ测试发送消息"; //定义需要发送的消息

            /**
             * 发送消息到MQ
             * 参数1：交换机名称，这里为空是因为不使用交换机
             * 参数2：如果不指定交换机，这个值就是队列名称；如果指定了交换机，这个值就是RoutingKey
             * 参数3：消息属性信息，null即可
             * 参数4：具体的消息数据内容、字符集格式
             */
            channel.basicPublish("","myQueue",null,message.getBytes(StandardCharsets.UTF_8));

            System.out.println("消息发送成功：" + message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
