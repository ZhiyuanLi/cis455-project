/usr/local/bin/hdfs dfs -rm -r output_index
/usr/local/bin/hadoop jar build/jar/index.jar input/index_input2 output_index
echo 'Job Input1'
echo '----------'
echo ''
echo ''
echo 'Job Output1'
echo '----------'
/usr/local/bin/hadoop dfs -cat output_index/part-r-00000