# CAAP



# 1 Vue

# 2 npm install 


# 3 npm run serve


Transcriptome automated processing tool environment and tool configuration tutorial
1 Configure conda environment
Configure according to the following link
https://zhuanlan.zhihu.com/p/489499097

2 Install the corresponding software
conda install fastqc

conda install trimmomatic

Installation of alignment software
conda install STAR

conda install Hisat2

conda install Bowtie2

conda install BWA

conda install Minimap2

Installation of quantitative software
Install featurecounts
conda install -c bioconda subread

Verify that featurecounts is installed successfully
featureCounts -h

conda install Stringtie

conda install HTSeq

seqtk trims fastq files
seqtk sample -s100 read1.fastq 0.5 > read1_subsampled.fastq
