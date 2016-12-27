import csv

def csv_to_vowpal(path_csv_parsed_file, path_vowpal_file,path_categories):
    items = []
    categories = []
    with open(path_categories, 'r') as f:
        reader = csv.reader(f,delimiter = '|')
        categories = list(reader)
    f.close()

    with open(path_csv_parsed_file, 'r') as f:
        reader = csv.reader(f,delimiter = '|')
        items = list(reader)
    f.close()

    for category in categories:
        data = []
        #print(category[0].strip())
        
        for item in items:
            label_value = "-1"
            if(item[3]==category[0].strip()):
                label_value = "1"
            label_name = str(item[1])
            label = label_value+" '"+label_name
            text_feat = 'f '+item[2].strip()+" "
            np_feat = 'headwords '+item[5].strip()+" ";
            line = [label,text_feat,np_feat]
            data.append(line)
            path_vowpal_file_final=path_vowpal_file+"_"+str(category[0])
        print(data)
        with open(path_vowpal_file_final,'w') as csvfile:
                writer = csv.writer(csvfile, delimiter= '|', quoting=csv.QUOTE_MINIMAL)
                writer.writerows(data)
        
path_csv_parsed_file_train = '../data/data_categories/vp/csv_train_parse.csv'
path_csv_parsed_file_test = '../data/data_categories/vp/csv_test_parse.csv'

path_vowpal_file_train = '../data/data_categories/vp_t/vowpal_train'
path_vowpal_file_test = '../data/data_categories/vp_t/vowpal_test'

path_train_categories = '../data/data_categories/train_categories'
path_test_categories = '../data/data_categories/test_categories'

csv_to_vowpal(path_csv_parsed_file_train,path_vowpal_file_train,path_train_categories)
csv_to_vowpal(path_csv_parsed_file_test,path_vowpal_file_test,path_test_categories)