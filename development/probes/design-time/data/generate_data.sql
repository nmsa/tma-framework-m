-- Select the last occurrence of each function
select CONCAT(FilePath, "!", NameMethod, "!", max(ID_Function)) as resourceName from FUNCTIONS_7_derby group by FilePath, NameMethod;
-- Total: 54,895


-- Select all the function metrics of 100 items
 select CONCAT(a.FilePath, "!", a.NameMethod), CountInput, CountLine,
CountLineBlank,
CountLineCode,
CountLineCodeDecl,
CountLineCodeExe,
CountLineComment,
CountOutput,
CountOutput,
CountSemicolon,
CountStmt,
CountStmtDecl,
CountStmtExe,
Cyclomatic,
CyclomaticModified,
CyclomaticStrict,
Essential,
Knots,
MaxEssentialKnots,
MaxNesting,
MinEssentialKnots,
RatioCommentToCode from FUNCTIONS_7_derby a inner join 
  (select FilePath, NameMethod, MAX(ID_Function) as max_id_function from FUNCTIONS_7_derby group by FilePath, NameMethod) b on a.ID_Function = b.max_id_function 
limit 100 -- this limit should be removed if we want to insert all items
INTO OUTFILE '/var/lib/mysql-files/functions.csv'
FIELDS TERMINATED BY ','
--ENCLOSED BY '"'
LINES TERMINATED BY '\n';
