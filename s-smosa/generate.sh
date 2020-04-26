#!/bin/bash
if [[ $OSTYPE == 'linux-gnu' ]]; then
	python_cmd='python'
else
	python_cmd='python2'
fi

if [ $# -eq 3 ] 
then
    echo 'Seed from ' $1 ' to ' $2 '- Threads = ' $3 
else
    echo "Invalid number of arguments"
    exit 1
fi

seed1=$1
seed2=$2
proc=$3

echo "For more than one version, separate the number with a comma"
echo "1 = MOSA"
echo "2 = boosted approach (SMOSA + MOSA)"
echo "3 = SMOSA"
read choice

rm -rf run.sh
echo '#!/bin/bash' >> run.sh
chmod 777 run.sh
for i in $(echo $choice | sed "s/,/ /g")
do
    if [ "$i" == "1" ]; then
	   ${python_cmd} scripts/MOSA.py mosa $seed1 $seed2 subjects.txt 1 $proc
	   echo 'chmod 777 mosa/scripts/ubuntu_EvoSuite_0.sh' >> run.sh
	   echo './mosa/scripts/ubuntu_EvoSuite_0.sh' >> run.sh
    fi
    if [ "$i" == "2" ]; then
        ${python_cmd} scripts/BOOST.py boost $seed1 $seed2 subjects.txt 1 $proc
        echo 'chmod 777 boost/scripts/ubuntu_EvoSuite_0.sh' >> run.sh
        echo './boost/scripts/ubuntu_EvoSuite_0.sh' >> run.sh
    fi
    if [ "$i" == "3" ]; then
        ${python_cmd} scripts/SMOSA.py smosa $seed1 $seed2 subjects.txt 1 $proc
        echo 'chmod 777 smosa/scripts/ubuntu_EvoSuite_0.sh' >> run.sh
        echo './smosa/scripts/ubuntu_EvoSuite_0.sh' >> run.sh
    fi
done
