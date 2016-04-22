/usr/local/bin/hadoop dfs -rm -r output1
/usr/local/bin/hadoop dfs -rm -r output2
/usr/local/bin/hadoop dfs -rm -r tmp
/usr/local/bin/hadoop dfs -rm -r pageRankOutput
/usr/local/bin/hadoop jar build/jar/all.jar input pageRankOutput 3
echo 'Job Input1'
echo '----------'
echo ''
ls -la input
echo ''
echo 'Job Output1'
echo '----------'
# /usr/local/bin/hadoop dfs -cat output1/part-r-00000
echo ''
echo 'Job Output2'
echo '----------'
# /usr/local/bin/hadoop dfs -cat output2/part-r-00000
echo ''
echo 'Job Output3 final sum'
echo '----------'
/usr/local/bin/hadoop dfs -cat tmp/iterated_sum/part-r-00000
echo ''
echo 'Job Output3 page rank'
echo '----------'
/usr/local/bin/hadoop dfs -cat tmp/iterated/part-r-00000
echo ''
echo 'Job Final Output'
echo '----------'
/usr/local/bin/hadoop dfs -cat pageRankOutput/part-r-00000