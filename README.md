## Linux服务搭建

### JDK安装

1、检查是否安装
```shell script
java -version
```
2、拷贝到指定安装目录
```shell script
cp -r /home/download/jdk-8u221-linux-x64.tar.gz /usr/local/java/
```
3、解压操作
```shell script
cd /usr/local/java/ #切换到指定目录
tar -zxvf jdk-8u221-linux-x64.tar.gz #解压文件
```
4、配置环境变量
```shell script
export JAVA_HOME=/usr/local/java/jdk1.8.0_221/
export JRE_HOME=/$JAVA_HOME/jre
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin

source /etc/profile #更新配置生效
```
5、验证是否成功
```shell script
java -version

#java version "1.8.0_221"
#Java(TM) SE Runtime Environment (build 1.8.0_221-b11)
#Java HotSpot(TM) 64-Bit Server VM (build 25.221-b11, mixed mode)
```

### Tomcat安装
1、复制安装文件到指定目录
```shell script
cp -r /home/download/apache-tomcat-8.5.53.tar.gz /usr/local/tomcat/
```
2、进入目录
```shell script
cd /usr/local/tomcat/
```
3、解压安装包
```shell script
tar -zxvf apache-tomcat-8.5.53.tar.gz cd /usr/local/tomcat/
```
4、配置环境变量
```shell script
export CATALINA_HOME=/usr/local/tomcat/apache-tomcat-8.5.53
export CLASSPATH=.:$JAVA_HOME/lib:$CATALINA_HOME/lib
export PATH=$PATH:$CATALINA_HOME/bin

#更新环境变量
source /etc/profile
```
5、启动Tomcat测试
```shell script
./catalina.sh start
http://${IP}:8080
```
6、tomcat启动缓慢
```shell script
vim /usr/local/java/jdk1.8.0_221/jre/lib/security/java.security

securerandom.source=file:/dev/./urandom #修改地址
```
7、配置多个tomcat
```shell script
vim /etc/profile
export TOMCAT_HOME=/home/server/vanessa/
export CATALINA_BASE=/home/server/vanessa/
export CATALINA_HOME=/home/server/vanessa/
source /etc/profile

vim /home/server/vanessa/bin/catalina.sh

#开头处添加
export CATALINA_BASE=$CATALINA_BASE
export CATALINA_HOME=$CATALINA_HOME

./startup.sh start
#访问：http://ip:port
```
### 防火墙操作
```shell script
systemctl start firewalld # 启动防火墙
systemctl stop firewalld # 关闭防火墙
systemctl restart firewalld # 重启防火墙 添加或删除端口必须重启
systemctl status firewalld # 查看防火墙启动状态
firewall-cmd --list-ports # 查看所有开放的端口
firewall-cmd --permanent --zone=public --add-port=${port}/tcp # 添加开放端口
firewall-cmd --permanent --zone=public --remove-port=${port}/tcp # 删除开放端口
```

### SVN仓库安装
1、安装SVN
```shell script
yum install subversion #云命令安装
```
2、查看安装版本
```shell script
svnserve --version
```
3、创建版本库
```shell script
mkdir svn #创建目录
svnadmin create /var/svn/proname #用svn管理员创建proname库
```
4、目录说明
```
proname
|——conf #是这个仓库的配置文件（仓库的用户访问账号、权限等）
|   |——authz #权限控制文件
|   |——passwd #帐号密码文件
|   |——svnserve.conf #SVN服务配置文件
|——db #所有版本控制的数据存放文件
|——format #是一个文本文件，里面只放了一个整数，表示当前文件库配置的版本号
|——hooks #放置hook脚本文件的目录
|——locks #用来放置subversion见艰苦锁定数据的目录，用来追踪存取文件库的客户端
```
5、账户设置
```shell script
cd /conf
vim passwod
#输入账户 账号=密码
account=passwod

#设置权限
vim auth
#设置权限 账号=权限 r读w写
account=rw

#修改svnserve.conf文件 vi svnserve.conf
#打开下面的几个注释：

anon-access = read #匿名用户可读
auth-access = write #授权用户可写
password-db = passwd #使用哪个文件作为账号文件
authz-db = authz #使用哪个文件作为权限文件
realm =/var/svn/目录名 # 认证空间名，版本库所在目录
```

6、启动svn版本库
```shell script
svnserve -d -r /var/svn/repo --listen-port=3690
```
7、停止SVN
```shell script
killall svnserve
```
卸载SVN
```shell script
yum remove subversion
```

###MySql安装
1、检查是否已安装
```shell script
rpm -qa | grep mysql

rpm -e --nodeps mysql-libs-5.1.73-5.el6_6.x86_64 #卸载

grep mysql #验证是否已删除完毕
whereis mysql
find / -name mysql #删除相关目录

cat /etc/group | grep mysql
cat /etc/passwd | grep mysql
#检查mysql用户组和用户是否存在，如果没有，则创建
groupadd mysql
useradd -r -g mysql mysql
```
2、下载Mysql
```shell script
wget http://repo.mysql.com/mysql57-community-release-el7-8.noarch.rpm
```
3、安装
```shell script
sudo yum install wget

sudo rpm -ivh mysql57-community-release-el7-8.noarch.rpm
sudo yum install mysql-server
```
4、获取初始化密码
```shell script
sudo grep 'temporary password' /var/log/mysqld.log #查看临时密码 如若为空请保证安装前已将mysql相关文件删除干净
systemctl restart mysqld # 重启服务
systemctl status mysqld # 查看服务执行状态
grep 'temporary password' /var/log/mysqld.log #再次查看临时密码
```
5、密码更改
```shell script
# sudo mysql_secure_installation #输入刚才的临时密码 All done!表示配置已经完成

mysqladmin -u root password '123456'
vim /etc/my.cnf #根目录的\etc\my.cnf文件中添加一行skip-grant-tables

systemctl restart mysqld.service #重启服务

mysql
update mysql.user set authentication_string=password('123456') where user='root';
flush privileges;
```
6、远程访问
```shell script
#连接数据库报错：1130-Host 'xxx' is not allowed to connect to this MySQL server解决
mysql -u root -p #连接服务器
show databases; #看当前所有数据库
#You must reset your password using ALTER USER statement before executing this statement.
alter user user() identified by '123456';

use mysql; #进入mysql数据库
select 'host' from user where user='root'; #查询下你的用户名为root下host字段的参数
update user set host ='%'where user ='root';
flush privileges; # 一定记得刷新
```
7、开启关闭忽略表名大小写
```shell script
vim /etc/my.cnf
lower_case_table_names=1 # 1、关闭 0开启
service mysqld restart
```
### memcached安装
1、memcached依赖于libevent库,因此需要先安装 libevent
```shell script
wget https://github.com/libevent/libevent/releases/download/release-2.1.8-stable/libevent-2.1.8-stable.tar.gz

wget http://www.memcached.org/files/memcached-1.5.10.tar.gz
```
2、安装libevent
```shell script
tar zxvf libevent-2.0.21-stable.tar.gz

cd libevent-2.0.21-stable

./configure --prefix=/usr/local/libevent

make && make install
```
3、安装memcached
```shell script
tar zxvf memcached-1.4.5.tag.gz

cd memcached-1.4.5

./configure --prefix=/usr/local/memcached --with-libevent=/usr/local/libevent

make && make install
```
4、启动memcached
```shell script
memcached/bin/memcached -m 256 -u root -p 12000 -d
memcached/bin/memcached -m 256 -u root -p 11211 -d
```
### zookeeper安装
1.0 下载zookeeper-3.4.9
```shell script
wget http://archive.apache.org/dist/zookeeper/zookeeper-3.4.9/zookeeper-3.4.9.tar.gz
```
1.1 解压zookeeper-3.4.9
```shell script
tar -zxvf zookeeper-3.4.9.tar.gz -C /usr/local/
```
1.2 在zookeeper-3.4.9中创建目录(根据自己zookeeper安装的路径进行修改路径)
```shell script
mkdir /usr/local/zookeeper-3.4.9/data
mkdir /usr/local/zookeeper-3.4.9/logs
```

1.3 对/usr/local/zookeeper-3.4.9/conf目录下的文件zoo_sample.cfg进行拷贝(根据自己zookeeper安装的路径进行修改路径)
```shell script
cd /usr/local/zookeeper-3.4.9/conf
cp zoo_sample.cfg zoo.cfg
```
1.4 修改配置文件
```shell script
dataLogDir=/usr/local/zookeeper/logs
dataDir=/usr/local/zookeeper/data

#配置集群
server.1={pi}:2888:3888
```
1.5 编辑/etc/profile并在文件末尾添加zookeeper配置
```shell script
#编辑文件
vi /etc/profile 

#添加下面内容(根据自己的路径进行修改)
export ZOOKEEPER_HOME=/usr/local/zookeeper
export PATH=$ZOOKEEPER_HOME/bin:$PATH

#生效修改的配置
source /etc/profile
```
1.6 将zookeeper加入开机自启
```shell script
#编辑文件
vi /etc/init.d/zookeeper

#加入以下内容（根据自己的环境修改响应的路径）

#!/bin/bash  
#chkconfig:2345 20 90  
#description:zookeeper
#processname:zookeeper  
export JAVA_HOME=/usr/local/java/jdk1.8.0_221
export ZOO_LOG_DIR=/usr/local/zookeeper/logs
case $1 in  
    start) su root /usr/local/zookeeper/bin/zkServer.sh start;;  
    stop) su root /usr/local/zookeeper/bin/zkServer.sh stop;;  
    status) su root /usr/local/zookeeper/bin/zkServer.sh status;;  
    restart) su root /usr/local/zookeeper/bin/zkServer.sh restart;;  
    *) echo "require start|stop|status|restart" ;;  
esac

#给定执行权限并做成服务和加入开机自启
chmod a+x /etc/init.d/zookeeper
chkconfig zookeeper on
chkconfig --add zookeeper

#相关命令
service zookeeper start
service zookeeper stop
service zookeeper status
service zookeeper restart
```
### dubbo-admin安装
#### dubbox项目github地址:https://github.com/dangdangdotcom/dubbox
```shell script
tar -zxvf apache-tomcat-8.5.30.tar.gz 

# tomcat重命名（系统里面已经有了一个tomcat为了区分所以就重命名了）
mv apache-tomcat-8.5.30.tar.gz /usr/local/dubbo-tomcat-8.5.30

#删除dubbo-tomcat-8.5.30中webapp中所有的内容
cd /usr/local/dubbo-tomcat-8.5.30/webapps/
rm -rf *
# 上传并且解压dubbo-admin-2.8.4.war,并且把目录命名root
unzip dubbo-admin-2.8.4.war -d ROOT
```
配置dubboproperties(根据实际情况修改用户名密码以及zookeeper地址)
```shell script
vi ROOT/WEB-INF/dubbo.properties
#dubbo.registry.address=zookeeper://ip:2181
#dubbo.admin.root.password=root
#dubbo.admin.guest.password=guest
```
启动tomcat服务(如果有两个tomcat的话需要修改端口否则有一个起不来)
```shell script
./usr/local/dubbo-tomcat-8.5.30/bin/startup.sh
```
浏览器中验证
IP:dubbo-tomcat-8.5.30端口
账号和密码则是dubbo.properties中的