##产品打特征标签功能
##需求内容：
给定特征标签文件，将产品库中所有产品关联特征标签。如果特征文件中某些特征有xxxx字样，则表示需要跨度查询，否则进行精确查询。
##实现方案:
(1) 对产品库创建索引。*注意：已经作了单复数处理，所以给定特征标签文件时，无需特别标出有单复数情况*
(2) 遍历特征标签，进行检索匹配，将产品ID对应标签ID结果保存JSON文件。
(3) 存储至数据库。*注意：仅供参考*
##工程说明
- data目录存放了词典文件、特征标签文件
- conf/system.properties 配置了项目运行需要的参数，包含数据源配置参数、索引创建依赖的参数
- `IndexBuilderMain.java` 创建索引入口
- `TagMatcherMain.java` 打标签入口
- `Store2DB.java` 转存数据库入口