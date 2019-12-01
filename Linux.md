# 								Linux

## Shell :

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

 