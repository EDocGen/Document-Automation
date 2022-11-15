using System;
using System.IO;
using TemplateBasedApplication.Helper.Constants;
using TemplateBasedApplication.Models.Dto;

namespace TemplateBasedApplication.Services
{
    public class FileUploadService
    {

        public void saveFileToFolder(FileUploadDto fileUploadDto)
        {
            if (!Directory.Exists(FileConstants.FILE_SAVE_FOLDER_PATH))
                Directory.CreateDirectory(FileConstants.FILE_SAVE_FOLDER_PATH);


            var filePath = Path.Combine(FileConstants.FILE_SAVE_FOLDER_PATH, fileUploadDto.file.FileName);

            try
            {
                using (FileStream fileStream = System.IO.File.Create(filePath))
                {
                    fileUploadDto.file.CopyTo(fileStream);
                }
            }
            catch (Exception ex)
            {
                throw new Exception($"File saving error: {fileUploadDto.file.FileName}");
            }
            
        }
    }
}
