import csv
import os
import os.path
from numpy import genfromtxt


def get_models(path_categories_train):
    base = '../data/data_categories/vp_t/vowpal_train_'
    categories_train = []
    with open(path_categories_train, 'r') as f:
        reader = csv.reader(f,delimiter = '|')
        categories_train = list(reader)
    f.close()
    
    for category in categories_train:
        str_path_train = base+category[0]
        #print(str_path_train)
        #str_vp_cm = 'vw '+ str_path_train+' --nn 4 --ngram 3 --passes 10 -l 0.7 -f '+ '../data/data_categories/vp_models/'+category[0]+'.model'
        str_vp_cm = 'vw '+ str_path_train+' -l 5 -c --nn 4 --ngram 2 --passes 25 --holdout_off -f '+ '../data/data_categories/vp_models/'+category[0]+'.model'
        #print(str_vp_cm)
        os.system(str_vp_cm)

def test_models(path_categories_test):
    base_model = '../data/data_categories/vp_models/'
    base_test = '../data/data_categories/vp_t/vowpal_test_'

    #print("test")
    categories_test = []
    with open(path_categories_test, 'r') as f:
        reader = csv.reader(f,delimiter = '|')
        categories_test = list(reader)
    f.close()
    #print(categories_test)

    cont =1    
    for category in categories_test:
        str_path_model = base_model+category[0]+".model"
        str_path_test = base_test+category[0]

        if(os.path.isfile(str_path_model)):
            cont+=1
            #str_vp_cm = 'vw '+str_path_test+' -t -i '+ str_path_model + ' -p '+ '../data/data_categories/vp_preds/'+category[0]
            str_vp_cm = 'vw -t --cache_file cache_test -i '+ str_path_model + ' -p '+ '../data/data_categories/vp_preds/'+category[0] + ' '+str_path_test
            os.system(str_vp_cm)
        else:
            print("no existe modelo")
    print(cont)

def get_results(path_categories_test):
    #print("test")
    base_test = '../data/data_categories/vp_t/vowpal_test_'
    base_preds = '../data/data_categories/vp_preds/'

    with open(path_categories_test, 'r') as f:
        reader = csv.reader(f,delimiter = '|')
        categories_test = list(reader)
    f.close()

    for category_test in categories_test:
        str_path_test = base_test+category_test[0]
        true_values = []
        id_colum_test = []
        if(os.path.isfile(str_path_test)):
            data = genfromtxt(str_path_test, delimiter='|',dtype=str)
            id_colum_test = data[:,0]
        else:
            print("no existe archivo de teste")

        pred_values = []
        str_path_pred = base_preds + category_test[0]
        existe = 1
        id_colum_pred = []

        if(os.path.isfile(str_path_pred)):
            #data = genfromtxt(str_path_pred, delimiter=',',dtype=str)
            with open(str_path_pred, 'r') as f:
                reader = csv.reader(f,delimiter = '|')
                id_colum_pred = list(reader)
            #id_colum_pred = data[:,0]
        
        else:
            print("no existe prediccion")
            existe = 0

        true_pos = 0
        true_neg = 0
        fals_pos = 0
        fals_neg = 0 
        if(existe):
            test_tam = len(id_colum_test)
            pred_tam = len(id_colum_pred)
            if(test_tam == pred_tam):
                for x in range(0,test_tam):
                    #print(id_colum_test[x]+ '-' + id_colum_pred[x][0])
                    
                    partes_sentence_test=id_colum_test[x].split(" ")
                    partes_sentence_pred=id_colum_pred[x][0].split(" ")
                    id_sentence_test = partes_sentence_test[1]
                    id_sentence_pred = "'"+partes_sentence_pred[1]
                    value_sentence_test = int(partes_sentence_test[0])
                    value_sentence_pred = float(partes_sentence_pred[0])
                    #print(id_sentence_test+ '|' + id_sentence_pred)
                    if(value_sentence_pred < 0):
                        value_sentence_pred =-1
                    else:
                        value_sentence_pred = 1
                    if(id_sentence_test != id_sentence_pred):
                        print("error")
                    else:
                        if(value_sentence_test == value_sentence_pred):
                            if(value_sentence_test == 1):
                                true_pos +=1
                            elif(value_sentence_test == -1):
                                true_neg +=1
                        else:#diferentes
                            #print("diferentes")
                            if(value_sentence_test == 1):
                                fals_neg +=1
                            elif(value_sentence_test == -1):
                                fals_pos +=1
                print('true_pos : '+ str(true_pos))
                print('true_neg : '+ str(true_neg))
                print('fals_neg : '+ str(fals_neg))
                print('fals_pos : '+ str(fals_pos))
            else:
                print('error '+str(test_tam)+'-'+str(pred_tam))

        


get_models('../data/data_categories/train_categories')
test_models('../data/data_categories/test_categories')
get_results('../data/data_categories/test_categories')