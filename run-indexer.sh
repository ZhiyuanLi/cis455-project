/usr/local/bin/hdfs dfs -rm -r /user/output_index
/usr/local/bin/hadoop jar build/jar/index.jar /user/input/index_input1 /user/output_index
echo 'Job Input1'
echo '----------'
echo ''
echo ''
echo 'Job Output1'
echo '----------'
/usr/local/bin/hadoop dfs -cat /user/output_index/part-r-00000