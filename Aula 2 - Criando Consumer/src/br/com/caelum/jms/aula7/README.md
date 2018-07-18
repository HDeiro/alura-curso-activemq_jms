# Processo de autenticação do ActiveMQ

Até agora usamos os nossos consumers e producers sem nenhuma restrição de acesso. Em outras palavras, não foi preciso se autenticar, não tem autorização e consequentemente qualquer um poderá acessar o ActiveMQ e usar tópicos e filas.

Como isso é algo importante no dia-a-dia, o ActiveMQ possui uma configuração que permite definir usuários, grupos e permissões. Vamos ver rapidamente como habilitar essa configuração.

Definindo usuários e grupos
Para definir usuários e seus grupos devemos usar plugins do ActiveMQ, no nosso caso um plugin de autenticação e outro de autorização. O primeiro passo é adicionar no arquivo conf/activemq.xml os usuários, senhas e seus grupos através do <simpleAuthenticationPlugin>.

No XML, logo após o elemento <broker ...> adicione:

```
<plugins>
  <simpleAuthenticationPlugin anonymousAccessAllowed="false">
    <users>
        <authenticationUser username="admin" password="admin" groups="users,admins"/>
        <authenticationUser username="user" password="senha" groups="users"/>
        <authenticationUser username="guest" password="senha" groups="guests"/>
    </users>
  </simpleAuthenticationPlugin>

  <!-- aqui vem ainda o authorizationPlugin --->
</plugins>
```

Repare que configuramos anonymousAccessAllowed="false". Isso significa que não podemos mais conectar ao ActiveMQ sem ter um usuário definido. Ao executar o código Java que realiza a conexão, recebemos uma exceção:

```
java.lang.SecurityException: User name [null] or password is invalid.
```

Faz todo sentido pois agora devemos nos autenticar!

Usando uma conexão com usuário e senha
Para definir o user name e password devemos mexer no código Java. Para nos conectar ao ActiveMQ sempre usamos o método createConnection() da interface ConnectionFactory. Esse método é sobrecarregado e possui uma versão que recebe o user name e password:

```
Connection connection = cf.createConnection("user", "senha");
```

Ao reiniciar o ActiveMQ com plugin de autenticação configurado podemos estabelecer uma conexão autenticada através do código Java!

Definindo permissões
Agora só falta dizer quais são as permissões de cada grupo. Existem 3 permissões disponíveis:

leitura (read)
escrita (write)
administrativa (admin)
Cada permissão fica associado com um tópico/fila e os seus grupos. Repare que usamos os grupos users, admins e guests. Sabendo disso, segue um exemplo de autorização para o tópico comercial:

```
<authorizationEntry topic="comercial" read="users" write="users" admin="users,admins" />
```

O grupo users pode ler, escrever e administrar o tópico e o grupo admin também possui a permissão administrativa.

A configuração completa deve ser feita dentro do arquivo conf/activenq.xml, dentro do elemento <plugins>. No nosso exemplo o grupo users tem acesso completo às destinations financeiro e comercial:

```
<authorizationPlugin>
    <map>
      <authorizationMap>
        <authorizationEntries>
          <authorizationEntry queue="fila.financeiro" read="users" write="users" admin="users,admins" />
          <authorizationEntry topic="comercial" read="users" write="users" admin="users,admins" />
          <authorizationEntry topic="ActiveMQ.Advisory.>" read="users,admins" write="users,admins" admin="users,admins"/>
        </authorizationEntries>
        <tempDestinationAuthorizationEntry>
          <tempDestinationAuthorizationEntry read="admin" write="admin" admin="admin"/>
        </tempDestinationAuthorizationEntry>
      </authorizationMap>
    </map>
</authorizationPlugin>
```

Além do nosso tópico comercial e a fila financeira existe uma terceira configuração relacionada com o tópico ActiveMQ.Advisory.. Esse tópico já existe por padrão no ActiveMQ e recebe mensagens administrativas (AdvisoryMessage) sempre que foi criado um consumer, producer e um novo destination. Mais infos em: http://activemq.apache.org/advisory-message.html

Uma vez feita toda a configuração dos plugins basta reiniciar o ActiveMQ. Além disso, como já falamos, devemos usar no código Java o usuário e a senha para nos conectar corretamente com a fila financeiro ou com o tópico comercial.

Você pode ver a configuração completa nesse arquivo: activemq.xml