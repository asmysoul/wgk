#下载项目依赖的jar包到本地库，并复制到项目的lib目录下
mvn dependency:copy-dependencies -DoutputDirectory=lib -DexcludeClassifiers=sources