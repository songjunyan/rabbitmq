### 交换器
- 1:常用交换器类型fanout、direct、topic、headers
- 2:不常用交换器类型System和自定义

#### fanout
- 它会把**所有发送**到**该交换器的消息**路由到**所有**与该交换器绑定的队列中
#### direct
- direct类型的交换器路由规则也很简单，它会把消息路由到那些**BindingKey和RoutingKey完全匹配的队列中**
#### topic
- 通过RoutingKey和BindingKey的匹配
- 两个key都是通过 . 来分隔字符串
- RoutingKey为一个标点符号 . 分隔的字符串
- BindingKey中可以存在两种特殊字符串"*"和"#"，用于做模糊匹配，其中"#"用来匹配一个单词，"*"用来匹配多规格单词（可以是零个）
#### headers
不实用性能差
根据发送的消息内容的headers属性进行屁诶，在绑定队列和交换器时制定一组键值对，当发送消息到交换器时，RabbitMQ会获取到该消息的headers，对比
其中的键值对是否完全匹配队列和交换器绑定时指的键值对，如果完全匹配则消息会路由到该队列，否则不会路由到该队列

### 常用方法讲解
#### 声明交换器exchangeDeclare方法
**Exchange.DeclareOk exchangeDeclare(String exchange,String type,
boolean durable,boolean autoDelete,
boolean internal,Map<String,Object> arguments)throws IOException**;
##### 参数说明
- exchange:交换器名称
- type:交换器类型
- durable：设置是否持久化 true持久化 false非持久化 设置为true后消息可以被存入硬盘重启机器不会丢失信息
- autoDelete： 设置是否自动删除，true为自动删除 **自动删除的条件**自动删除的前提是至少有一个队列或者交换器与这个交换器绑定，
之后所有与这个交换器绑定的队列或者交换器都与此解绑
- internal:设置是否内置如果为true则标示是内置交换器，客户端程序无法直接发送到这个交换器中，**只能通过交换器路由到交换器这种方式**
- argument: 其他结构化参数


#### 声明队列
Queue.DeclareOk queueDeclare(String queue,boolean durable,boolean exclusive,boolean autoDelete,
Map<String,Object> arguments)throws IOException;
#### 参数说明
- 如果不带任何参数的queueDeclare方法默认创建一个RabbitMQ 命名的蕾丝amq.gen-SLDJOI2323KIU名称，这种队列也称之为匿名队列 排他的、自动删除的、持久化的队列
- queue：队列的名称
- durable: 设置是否持久化，为true时候设置为持久化，重启也不会丢失信息
- exclusive: 设置是否排他。为true则设置队列为排他的，如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除这里需要注意三点
1.排他队列是基于连接可兼得，同一个连接的不同信道是可以同时访问同一连接创建的排他队列；
2."首次"是指如果一个连接已经声明了一个排他队列，其他连接不允许建立同名的排他队列的，这个与普通队列不同
3.即使该队列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除，这种队列适用于一个客户端同时发送和读取消息的应用场景

-autoDelete: 设置是否自动删除。为true则设置队列为自动删除。
自动删除前提是至少有一个消费者连接到这个队列，之后#**所有**与这个队列连接的消费者都断开时才会自动删除，
不能把这个参数错误地理解为："当连接到此队列的所有客户端断开时这个队列自动删除

-arguments：设置队列的其他一些参数

#### 注意要点：
生产者消费者都能够使用queueDeclare来声明一个队列，如果消费者在同一个信道上订阅来另一个队列，就无法再声明队列了。必须先
取消订阅，然后将信道置为传输模式，之后才能声明队列

