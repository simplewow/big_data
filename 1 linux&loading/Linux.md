



## 1.目录：



安装系统

简单命令

文件系统

文本操作

VI

正则表达式

文本编辑

用户管理

权限管理

安装软件

脚本编程



------

------

#补充shell设置：

​	可以先设置连接

​	可以调整字体，

​	可以将选中的复制到剪切板，右键是粘贴







## 2.Create:



安装操作系统  ---->   配置网络  ---->   克隆集群



------

------



## 3.Command:



-   以查看命令为例：

```bash
type：命令类型
#外部命令 & 内部命令：   外部在磁盘 path,内部在kernel，shell (内存两部分，kernel和可以用的)
#是个程序，子进程，跑完自动挂掉。

file：文件类型  
#外部命令是可执行文件或者脚本文件（#！...）

echo：打印到标准输出
	-n  -e（识别转义） 
$PATH： 环境变量：路径
#可以查看path echo $PATH


help：内部命令帮助
	可以直接，查看有多少内部命令
   
man： 外部命令帮助
	yum install man man-pages -y 
	类别：
		1：用户命令(/bin, /usr/bin, /usr/local/bin)
		2：系统调用
		3：库用户
		4：特殊文件(设备文件)
		5：文件格式(配置文件的语法)
		6：游戏
		7：杂项(Miscellaneous)
		8: 管理命令(/sbin, /usr/sbin, /usr/local/sbin)
	man utf-8
	#ASCI：一个字节，8位，但是第一位恒为0，所以表示0到127个字符
	#000.... 几个数字1代表用几个字节表示。 例如 1100.... 1000..  11用两个  字符流
	

whereis : 定位命令位置

date:
	-s 设定时间  date -s 20120523 
			   date -s 01:01:01 
			   date -s "20120523 01:01:01"  
	格式化输出：
		       date +"%Y-%m-%d"    (打印现在2009-12-07)
	
		       
	-d  查看     date -d "2009-12-12" +"%Y/%m/%d %H:%M.%S"  (配合+,格式化查看 2009/12/12 00:00.00）
```



-   查看内容：

```bash
df -h
#查看分区的储存情况

du -sh ./*
#* 代表分别统计该目录下子的大小

ls -l   =  ll
#-rw-r--r--   1  root   root   194    Nov 22 01:20   test
#ls -l的输出内容一般有七栏 
	#1，文件类型 -普通文本文件，
		#d目录， bc设备文件（b字节，c字符），l 链接 , s(socket) , p管道
	#2.[文件数] 被链接数量
	#3，权限
	#4，大小，时间，名称


```





------

------



## 4.Filesystem:



### 4.1 Structure:

目录树结构：虽然底层分区不一样，但是再抽象一层，全给封装成统一结构的虚拟树结构。

#例如第一分区的boot 目录  挂在第三分区的 /目录下。



结构：

```
–/boot: 系统启动相关的文件，如内核、initrd，以及grub(bootloader) 引导文件

–/dev: 设备文件 

–/etc：配置文件

–/home：用户的家目录，每一个用户的家目录通常默认为/home/USERNAME

–/root：管理员的家目录；

–/lib：库文件

–/media：挂载点目录，移动设备

–/mnt：挂载点目录，额外的临时文件系统

–/opt：可选目录，第三方程序的安装目录

–/proc：伪文件系统，内核映射文件

–/sys：伪文件系统，跟硬件设备相关的属性映射文件

–/tmp：临时文件, /var/tmp

–/var：可变化的文件

–/bin: 可执行文件, 用户命令

–/sbin：管理命令

```



### 4.2 Command:

```bash
df：显示磁盘使用情况
du：显示文件系统使用情况

ls：显示目录 
cd：切换工作目录
	#cd - 上一次
    #cd ~god god用户家目录 （~扩展）
    
pwd：显示当前工作目录


mkdir：创建目录             
	#深度：mkdir –p ./a/b 
	#横向：mkdir {a ,b}  (只建一级目录不用./)  (不能有空格，以及单双引号（否则不扩展了）)

rm：删除                    
	#-rf  a/b      （直接a不用前面的或者./）

cp：拷贝 
	#-r
#cp file newname && cp /file /new
	
mv：移动 （改名）
	#没有r
	
ln：链接 
#ll –i  实际地址 
	#硬：ln a_1 a_2   文件类型不变，实际地址一样,数量加1
	#软：ln -s	文件类型为 l,实际地址不同。 
	#异同:
		#本质：一方修改，都会变。  
		#硬删了a_1，文件没影响。软删了，爆红(类似留下快捷方式)

stat：元数据（属性）
	#时间：浏览时间（Access），内容修改（modify），元数据（change）
	
touch：
#三时间统一和创文件

```



------

------



## 5.Operation:



显示文件内容

```bash
cat
#直接显示出来

more 分屏
	#空格下一面，回车一行，b回翻，看完直接退出

less 分屏
	#空格下一面，回车一行，b回翻，看完q退出

head tail
#游标 ：head -4 file | tail -1  


```



管道

```bash
echo "/" | xargs ls -l
#后面要是有输入的命令  
```

 

------

------



## 6.VI:



### open_close:

```bash
#打开：
vim somefile  
vim +#      :第#行                !
vim +       :最后
vim +/what  :第一次匹配到的行的行首  !

#关闭：
q           #末行模式（esc）		 ！
ZZ          #直接
```



### mode:

–编辑模式：按键具有编辑文本功能：默认打开进入编辑模式

–输入模式：按键本身意义

–末行模式：接受用户命令输入



#### 1,转化: 

​	末行  <--> 编辑 <--> 输入

```bash
#编辑-->输入：
	i: 字符前面                  !
	a: 字符后面                  !

	o:光标行下，新建一行           !
    O:光标行上，新建一行	          !
    
	I：光标行首
	A：光标行尾
	
#输入-->编辑：
ESC

编辑-->末行：
：

末行-->编辑：
ESC, ESC

```



#### 2,编辑：

​	#光标

```bash
#字符
h: 左；j: 下；k: 上；l: 右         !

#单词
w: 下词首						  !
e: 当前或下词尾
b: 当前或前词首

#行内
0: 绝对行首
^: 行首的第一个非空白字符           !
$: 绝对行尾						  !

#行间                            !
G:文章末尾
3G:第3行
gg:文章开头

翻屏                             !
ctrl：f，b


```

​	#操作

```bash
末行也行
#删除&替换单个字符
x:删除光标位置字符
3x:删除光标开始3个字符
r:替换光标位置字符

#删除命令 ： d 
dw，2dd, d+光标组合啥的

#复制粘贴&剪切（d 内存缓冲区） 	
yw，yy
p P

#撤销&重做							!
u   撤销
ctrl+r  重做 撤销的操作
.  重复上一步的操作

```



#### 3,末行:

```bash
#set：设置             !
set nu     #number
set nonu   #nonumber
set readonly

#/：查找         		!            
/word    	#查找到行首  
			#如果直接在编辑下/，直接开启末行，并且找到该词前               
n，N        #下上


#!：执行命令			  !
!ls -l /

#1,$d ：命令组合 #删除1到最后行

#s: 查找并替换         !
1,4s/a/b/gi

s/str1/str2/gi:
范围:
	n：行号
	.：当前光标行
	+n：偏移n行
	$：末尾行，$-3     !(在编辑中是一行内，这里是全文)
	%：全文

分割：
	临近s命令的第一个字符为边界字符：/，@，#

附加：
	g：一行内全部替换     (默认是一行出现第一个)
	i：忽略大小写


```

 

------

------



## 7.Regex:



### 1,grep:

```bash
grep word file
#返回满足的行
	#-v 反显示 ， -e 使用正则
	#通配符： *：0到多  ?:1


grep -[acinv] '搜索内容串' filename 
	-a 以文本文件方式搜索 #不要忽略二进制的数据
	-c 计算找到的符合行的次数 
	-i 忽略大小写 
	-n 顺便输出行号 
	-v 反向选择，即找 没有搜索字符串的行 
	其中搜索串可以是正则表达式! 

	
```



### 2,regex：

```bash
#匹配操作符：

\                     转义字符
. 	                  匹配任意单个字符
[1249a]，[^12],[a-k]  字符序列单字符占位
^                     行首
$                     行尾
\<,\>                 单词首尾边界

|                    连接操作符            。。。。。
()                   选择操作符			   。。。。。
\n    	             反向引用              。。。。。
	#"\(oo\).*\(xx\)\2\1 "	


#重复操作符：
*      	匹配0到多次。 

?      	匹配0到1次。                        。。。。。
+     	匹配1到多次。                        。。。。。
{n}   	匹配n次。                           。。。。。
{n,}  	匹配n到多次。                        。。。。。
{n,m}      匹配n到m次。                      。。。。。

#与扩展正则表达式的区别:grep basic  （扩展的 -E 或者 egrep。。。。）
	#否则直接当做字符匹配了
\?, \+, \{  and   \|, \(, and \)

#匹配任意字符：
.*

```



------

------



## 8.Editing:



### 1,cut:

```bash
#cut：显示切割的行数据

	f：选择显示的列
	s：不显示没有分隔符的行
	d：自定义分隔符

cut -s -d' ' -f1-3 file
```



### 2,sort:

```bash
#sort：排序文件的行

	n：按数值排序  #默认字典序
	r：倒序
	t：自定义分隔符	
	k：选择排序列
	u：合并相同行

sort -t' ' -k2 -nr file
```



### 3,wc:

```bash
#wc: 统计 

wc –[lLcm] file  
	-l (行数) ，L(长行里面字数), c（字节）,m(字符)


#cat file   | wc -l  #直接wc file  显示3  filename   
#ls -l /ect | wc -l

```



### 4,sed:

```bash
#行编辑器  &相当于VI(全屏阻塞)末行模式 

sed  [options]  'AddressCommand'  file 

#options:
	-n: 静默模式，不打印出来
	-i: 直接修改原文件
	-e SCRIPT -e SCRIPT:可以同时执行多个脚本
		#sed -n -e'/sad/p' -ne'/asas/d' abc  (e空不空都行)
	-f /PATH of SCRIPT ：用脚本来处理  ？脚本sed还没会
	-r: 表示使用扩展正则表达式

#Address：
	可以没有
	给定范围
	查找指定行/str/
	
#Command：
	d: 删除行
	p: 显示行
	=：显示文件行号
	a \string: 行后加新行，内容为string
		#\n：可以用于换行
	i \string: 行前面加新行
	
	
	
	r FILE: 将指定的文件的内容添加至符合条件的行处
	w FILE: 将地址指定的范围内的行另存至指定的文件中;
	
	s/pattern/string/修饰符: 查找并替换，默认只替换每行中第一次被模式匹配到的字符串
		g: 行内全局替换
		i: 忽略字符大小写
		#s///， s# # #, s@ @ @	
		#配合\(\), \1, \2
		
		#扩大查找范围，留下想留下的。
		

#多范围中间用 ,
#多命令用{=;p}  ？？？  或者 -e

#sed -n 's/\(id:\)[0-6]\(:initdefault:\)/\15\2/ig'  inittab

```



### 5.awk:

```bash
'''
awk是一个强大的文本分析工具。
相对于grep的查找，sed的编辑，awk在其对数据分析并生成报告时，显得尤为强大。
简单来说awk就是把文件逐行的读入，（空格，制表符）为默认分隔符将每行切片，切开的部分再进行各种分析理.
'''
```



```bash
awk -F '{pattern + action}' {filenames}
		  1范围     2操作
	
	0 支持自定义分隔符
    
	1 支持正则表达式匹配
	
	支持自定义变量，数组  a[1]  a[tom]  map(key)
	
	2 支持内置变量
		ARGC               命令行参数个数
		ARGV               命令行参数排列
		ENVIRON            支持队列中系统环境变量的使用
		FILENAME           awk浏览的文件名
		FNR                浏览文件的记录数	
		FS                 设置输入域分隔符，等价于命令行 -F选项
	!!	NF                 浏览记录列数
	!!	NR                 已读的记录数
		OFS                输出域分隔符
		ORS                输出记录分隔符
		RS                 控制记录分隔符
		
	2 支持函数
		print、split、substr、sub、gsub
		
	2 支持流程控制语句，类C语言
		if、while、do/while、for、break、continue

```



例题：awk对每行都操作一遍。

```bash
#1，只是显示/etc/passwd的账户:CUT

awk -F':'  '{print $1}'  passwd


#2,只是显示/etc/passwd的账户和账户对应的shell,而账户与shell之间以逗号分割,而且在所有行开始前添加列#名name,shell,在最后一行添加"blue,/bin/nosh"（cut，sed）

awk -F':' 'BEGIN{print "name,shell"} {print $1 "," $7} END{print "blue,/bin/nosh"}' passwd

#3，搜索/etc/passwd有root关键字的所有行

awk  '/root/ { print $0}'   passwd

4.统计/etc/passwd文件中，每行的行号，每行的列数，对应的完整行内容

awk  '/root/ { print NR "\t" NF "\t" $0}'   passwd
```







实战：

```
统计报表：合计每人1月工资，0：manager，1：worker
Tom	 0   2012-12-11      car     3000
John	 1   2013-01-13      bike    1000
vivi	 1   2013-01-18      car     2800
Tom	 0   2013-01-20      car     2500
John	 1   2013-01-28      bike    3500
```

```bash
awk ' { split($3,date,"-");\
		if(date[2]=="01")\
			{   name[$1]+=$5 ; \
				if($2 == "0"){role[$1]="M"}\
				else{role[$1]="W"} } } \
		END{ for(i in name) {print i "\t" name[i]"\t" role[i]} }'   awk.txt 

#每行处理用{} 弄在一起，一行{}内多命令 ; ， 最后END
```





#括起来操作全单引号:  ’    '   ，有字符双引号“  ” 

 #init 文件  启动  3 文字，5图形



------

------



## 9.Right:



**三位一体的概念:    用户 ， 资源  ，  权限**

#操作系统的root和普通，，资源的管理和普通





用户

```bash
#1,创建
useradd use01
passwd  use01
useradd use01
passwd  use01
#服务器必须设置密码

#2,切换
su use01
#root切不要密码

#3,改组 
groupadd useshare
usermod -a -G useshare use01
#user增加，组。

#
```



资源

```bash
#资源所属组
chown user:group  file  (不改可省略)
```



权限

```bash
#字符型和数字（421）
	
	#改完权限还没刷新	
	#文件的，，x  可执行
	#文件夹，，x  cd


chmod [ugo][+-][rwx] directory/file

chmod [数字] directory/file


#当创建完目录，再创建文件时，默认文件是属于use01这个组的，
	#一种方法可以选择修改权限就行 （因为一般人还不能进入这个目录）


```





------

------



## 10.Install

src —->  rpm ——> yum

### 1,src:

#编译安装

```
#编译安装
配置文件：Makefile
编译，安装命令：make


#案例：编译安装nginx。
	下载源码
	tar xf 解压  (撕，定位文件，用这两个就行了) 
	tar -zxvf japan.tar.gz -C /tmp/
	README
		./configure --prefix=/path ：有错就按照要求改 创建Makefile
		vi Makefile
		make：(实际上读Makefile) 编译
		make install (make 打开makefile 找install ) 

#注意：
	编译环境    
	软件依赖
	配置项
```



### 2,rpm:

#包（要自己管理）  redhat packet manage

```bash
#安装
rpm安装：一般i
	-i filename
	--prefix
	
rpm升级：
	-Uvh  （v 打印）
	-Fvh
	
rpm卸载: (包名 qa出来的)
	-e PACKAGE_NAME

```

```
#查询
rpm -qa : 查询已经安装的所有包

rpm -ql PACKAGE_NAME: 查询指定包安装后生成的文件列表

pm -qf /path/to/somefile: 查询文件是由哪个rpm包安装生成的	（逆向）

```



```
#例子  （JAVA 安装后有些环境变量

-qa包(配合管道)  , -ql（有啥文件）

有的释放有软连接，但是有的没有，要在 /etc/profile配置   （PATH）
	#（（末行模式：! ls） 查看地址
	export JAVA_HOME=/usr/java/jdk1.7.0_67
	export PATH=$PATH : $JAVA_HOME/bin(：附加)
	
配置好，重置资源
source file

#装了有记录，，数据库记录
#包安装：。。。缺点要自己下依赖
```



### 3,yum:

#仓库



#基本知识：

```bash
类似C / S    yum不同是计算在客户端自己算
	仓库有包和元数据（packages  repodata） （/mnt  仓库在本地的地方） 
	客户先下元数据，和本地自己算缺啥。

#yum
命令：
	yum repolist     看仓库
	yum clean all    清
	yum makecache   清缓存
	yum update
查询：
	yum list		包
	yum search

安装&卸载：
	yum install 
	remove|erase

分组：
#把包弄成组了   ”“引用当做一个整体  防止空格
	yum grouplist
	yum groupinstall
	yum groupremove
	yum groupupdate

```



#仓库变化：

```
#换仓库
#cd  /etc/yum.repos.d/ (仓库信息在这，yum install会找这里)

#1，epo国内源：
http://mirrors.aliyun.com
	centos-->help
	before:yum install wget           ---一定要先下
	......                            ---接下来按照help
	
#2，本地库：

mount /dev/cdrom1    /mnt  (同时看虚拟机是否选了挂的镜像)
#先挂个本地库盘(例子为一个base库)，包不全，但是元数据写的全

cd /etc/yum.repos.d/  
mv  CentOS-Base  local.repo  
#留这一个，改名，后缀别错

Vi local.repo     
	dgg(从光标到开头删) ,dG(从光标到结尾删) , dd（一行） , D（光标以后删到行尾）

#留下并修改如下：

	[local]     #repo id
	Name=  		#repo name
	baseurl=file:///mnt    （yum再找repodata,开启下载）
	gpgcheck=0


yum clean all
yum makecache   
	

#3 集群仓库：
#做服务器

```



例子：**中文显示，查看中文文档** (临时赋值)

```
yum 的 repo 变成aliyun  || 本地DVD

yum grouplist
yum groupinstall “Chinese Support“   
#装中文

echo $LANG
	#en_US.UTF-8
LANG=zh_CN.UTF-8
#改中文

#增加epel的repo仓库：
	http://mirrors.aliyun.com        
	epel>>>>>help
	wget centos6.......

yum clean all
yum makecache

yum search man-pages
yum install man man-pages man-pages-zh-CN（epel仓库才有这个包）

man bash

#流程是先找 /etc/（这个仓库信息）, 这个会告诉你仓库在哪.
```



------

------





## 11.Shell :



### 	What  is bash : 

–解释器，启动器

​	•用户交互输入

​	•文本文件输入

​		

读取方式：文件实行方式。

​	–当前shell：**source/.**      #这两个一样 文本文件        

​	–新建子shell：**/bin/bash file**    \#**! /bin/bash chmod**脚本（特殊文本，解释器）

​		

-命令概念总结： shell,  外部， 函数 func(){}



-else:

​	**./file  ， func,**   #how to use

​	pstree,exit    #show  processing



------



### Redirection:

#### 		-重定向：不是命令

–一切皆文件

–程序自身都有I/O

​	•0：标准输入

​	•1：标准输出                    

​	•2：错误输出

-控制程序I/O位置  ： **/proc/$$/fd**       (ll 出来文件操作符指向)

​												

#### 		–1,实战：A界面，输出到B

**exec  `n>`    /dev/pts/1**         （当连接两次时候，会创建新的shell 1）

**exec  `n>&`  last**   （指回去）



#### 		-2,输出

​	（thero A :    绑定顺序：从左到右）

•one file :  **1> file     >>         (2>& 1 )**

•more file：

​	•diff:   **ll /usr /god 1> file1 2> file2 **              (god don’t exit)

​	•same:

​		–**ll /usr /god  1>   file   2> &  1**     -A

​		–**ll /usr /god   > &   file**    -特殊（& usually follow with file_num_discribe）

-   [x]  –**ll /usr /god &>  file**	

    

#### 		-3,输入

• read: (command, stuff the proc to input thing to init v until meet \n) ,,  **echo $v**

•Cat :(not sensitive with \n)

​	•<<<:        **read v 0<<< “asdsd”**

​	•<< : 		**read v 0<< oo**

​		»(u could input some rows,end with oo, but read\n 

​		»Solution :**cat v 0<< oo**

​			 »Or by .sh :

​				»**Vi a.sh**

​				**»cat v 0<< oo: then input** 

​	•< :           **cat 0< file =  cat file**		

socket  case:  (本来exec  用command 压如bash 结束后 挂掉连接。。。

​						但是文件操作符，不是命令。然后改变指向。 )

​	**exec  8<>  /dev/tcp/www.baidu.com/80**

​	**echo -e "GET / HTTP/1.0\n" >& 8**          请求头协议：请求，URL，协议

​	**cat <& 8**		



------



### Variable：

#### 		本地：

随shell,  **v=god**



#### 		 局部：

随函数，**local v=god**



#### 		位置：

（.sh ）

**echo $# (个数)**，，，，，(*,@ 列表，前面一个串，后面单独)   (1,${12})

​											（$$ 优先级大）    

​											   ($BASHPID  真实优先小)        管道：是创建了左右子进程

​											（$?）      0:成功



#### 		环境：

export

​	定义:  父子进程 变量改变 互不影响           `***COW (copy on wirte)***`

​	先是类似指向赋值，当变量需要改变时候，再拷贝，保证互不影响

​	验证： 

​		vi  a.sh**

​		**/bin.bash   a.sh**  &   (后台运行)



------



###  Quote & Expansion:

#### 引用：

​	**a=1**				主要可以表达弄成一个元素

​	1）“ ”，弱，可以扩展。  **echo “ $a” **      ——1	

​	2)  ‘  ’  ,  强， 不可嵌套。 **echo  ‘ $a ’**	  ——$a

​	3) 花括号不能被引用 。

​	4）写括号，只能1）		**echo "  \\" $a  \\"  "**    ——“ 1 ”



#### 替换：

​	表示是命令，值取出来，不输出： **\` ls -l   /`   or    $ ( ls -l   /  )**    可嵌套



------



### State &  Logistic:

#### 退出状态：

​	**echo  $?​**          ：0为成功，为真。 



#### 逻辑判断：

​	&&  and ||    ： 常用在执行命令，前者先假就停，后者先真就停。



------



### Expression：

#### 算术表达式：

​	**let c =$a+\$b **  or **c= $ (( a+b ))**             =后面都不空格



#### 条件表达式：

​	**test c   or [ c ]**            							 命令要留白  test = [ ]

​																	  都用help 查看



#### 总结例题1：

​	–添加用户    –用户密码同用户名   –静默运行脚本  –避免捕获用户接口   –程序自定义输出  ：要求重定向了

​	

```bash
#!/bin/bash           

[ ! $# -eq 1 ] && echo "args error " && exit 1
#看参数个数是不是一个。 能same就same && or  ||

id $1  &> /dev/null && echo "user exist " exit 2
#看是否存在。  这个地址类似为垃圾站 。 

useradd $1 &> /dev/null && echo $1 | passwd --stdin $1 &> /dev/null && echo "ok " && exit 0
# 添加用户和密码。 要标准输入重定向

echo "other error"

exit 3


```



------



###  control ：

​	help 来查看,,一行分号，多行不要

#### 	if:

​		**if [ 3 -gt 8  ]; then echo yes; else echo no ; fi**



#### 	**for:**

​		in [ ] :   **for i in “asa sds”  “sd  dsds”; do echo i; done**  # 空格换行切割

​		(  (	:    **for ( ( i=0;i<4;i++) );   do echo i; done**                # ( (自动取值



#### 	while:

​		**while COMMANDS; do COMMANDS; done**

​	

#### 	case:

​		**case a in**

​		**1) echo "You select 1"**
​		**;;**
​		**3)  echo "You select 2"**
​		**;;**

​		***) echo “a”**

​		**;;** 

​		**esac**

​	

#### 	**总结例题2：**

​	–用户给定路径  –输出文件大小最大的文件  –递归子目录   ：用一定现有的。

​	

```bash
#!/bin/bash
#新的shell 执行，省得exit 退出了。

old=$IFS
IFS=$'\n'
#for 的 用三个空格分隔。 $'' 取ASC

for i in `du -a $1 | sort -nr`;do
#du -sh ./* (分个目录统计,-s总和)  -a 每个都算。 ` ` 取值
        filename=`echo $i | awk '{print $2}'`
        #awk 分割
        if [ -f $filename   ];then
        #[] -f 判断
                echo $filename
                exit 0
        fi
done
IFS=$old
echo "no found"
eixt 1

```

​	

#### 总结例题3：

•循环遍历文件每一行:流程控制语句  –定义一个计数器num  –打印num正好是文件行数

```bash
#!/bin/bash
old=$IFS
IFS=$'\n'

#for in
num=0
for i in `cat test.txt` ;do
	echo $i
	((num++))
done
echo num:$num
IFS=old

#for((
num=0
lines=`cat test.txt | wc -l`
for (( i=1 ;i<=lines ; i++));do
	head -$i test.txt | tail -1 
    ((num++))
done
echo num:$num

#while 
exec 8<&0
exec 0< test.txt
#输入变成文件
num=0
while read line;do
#read v XXX  让XXX赋值给v，每次读一行
	echo $line
    ((num++))
done
echo num:$num
exec 0<&8
#不然一会输入口直接关闭，然后断开连接。。。子进程是同一个，和另外连接不一样


#while 的 $ 和上面一样  (简写)
num=0
while read line;do
	echo $line
    ((num++))
done 0< test.txt
echo num:$num
exec 0<&8

#管道
cat testa | while read line; do echo $line; ((num++)) ;echo $num;  done

#创了新进程，num 父进程还是不变。改进：写进文件



```



#### 知识总结：

•1，花括号 mkdir -p sdfsdf/{a,b,c}sdfsdf

•2，波浪线 cd ~god       #用户名前缀  root用户

•3，变量&参数 $  $$   $(）

•4，命令替换 ls -l   ` echo $path  反引号

•5，算术扩展 num=$((3+4))

•6，word拆分，$IFS   制表符，换行 ，空格

•7，路径 *（零到多个任意字符）？

•8，引用删除  echo "hello"

•*，重定向 > 

​		



​		





















​						

​						

 