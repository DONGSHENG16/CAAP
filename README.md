# CAAP


## 1 Client
(1) npm install 
(2) npm run serve

Notice: The node version needs to be greater than v16.20.0.

## 2 Server
Notice: Java version greater than 17.0.

## 3 Remote server environment configuration

Transcriptome automated processing tool environment and tool configuration tutorial

(1)Configure conda environment
Configure according to the following link
https://zhuanlan.zhihu.com/p/489499097

(2)Install the corresponding software
conda install fastqc
conda install trimmomatic

(3)Installation of alignment software
conda install STAR
conda install Hisat2
conda install Bowtie2
conda install BWA

conda install Minimap2

（4）Installation of quantitative software
Install featurecounts
conda install -c bioconda subread

Verify that featurecounts is installed successfully
featureCounts -h

conda install Stringtie
conda install HTSeq

（5）seqtk trims fastq files
seqtk sample -s100 read1.fastq 0.5 > read1_subsampled.fastq
