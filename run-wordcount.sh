/usr/local/bin/hdfs dfs -rm -r output
/usr/local/bin/hadoop jar build/jar/WordCount.jar input/ output
echo 'Job Input1'
echo '----------'
echo ''
echo ''
echo 'Job Output1'
echo '----------'
/usr/local/bin/hadoop dfs -cat output/part-00000