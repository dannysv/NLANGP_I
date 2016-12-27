import os

str_command_1 = 'python3 sem_to_csv.py'
str_command_2 = 'javac -cp ../jars/stanford-parser.jar:../jars/stanford-parser-3.7.0-models.jar:../jars/google-collections-1.0.jar:. Parser.java'
str_command_3 = 'java -cp ../jars/stanford-parser.jar:../jars/stanford-parser-3.7.0-models.jar:../jars/google-collections-1.0.jar:. Parser'
str_command_4 = 'python3 csv_to_vow.py'
str_command_5 = 'python3 app_nlangp.py'

os.system(str_command_1)
os.system(str_command_2)
os.system(str_command_3)
os.system(str_command_4)
os.system(str_command_5)