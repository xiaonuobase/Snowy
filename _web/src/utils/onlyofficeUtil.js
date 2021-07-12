export function handleDocType(fileType) {
  let docType = ''
  const fileTypesDoc = [
    'doc', 'docm', 'docx', 'dot', 'dotm', 'dotx', 'epub', 'fodt', 'htm', 'html', 'mht', 'odt', 'ott', 'pdf', 'rtf', 'txt', 'djvu', 'xps'
  ]
  const fileTypesCsv = [
    'csv', 'fods', 'ods', 'ots', 'xls', 'xlsm', 'xlsx', 'xlt', 'xltm', 'xltx'
  ]
  const fileTypesPPt = [
    'fodp', 'odp', 'otp', 'pot', 'potm', 'potx', 'pps', 'ppsm', 'ppsx', 'ppt', 'pptm', 'pptx'
  ]
  if (fileTypesDoc.includes(fileType)) {
    docType = 'text'
  }
  if (fileTypesCsv.includes(fileType)) {
    docType = 'spreadsheet'
  }
  if (fileTypesPPt.includes(fileType)) {
    docType = 'presentation'
  }
  return docType
}
