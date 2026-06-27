const fs = require("fs");

function deleteFile(file) {
  if (file && fs.existsSync(file.path)) {
    fs.unlinkSync(file.path);
  }
}

function deleteFiles(files) {
  console.log("files in delete function:  ", files);

  //files.forEach((file) => deleteFile(file));

  if (files.image) {
    files.image.forEach((file) => deleteFile(file));
  }

  if (files.coverImage) {
    files.coverImage.forEach((file) => deleteFile(file));
  }

  if (files.link) {
    files.link.forEach((file) => deleteFile(file));
  }

  if (files.pkVideoArray) {
    files.pkVideoArray.forEach((file) => deleteFile(file));
  }

  if (files.pkImageArray) {
    files.pkImageArray.forEach((file) => deleteFile(file));
  }

  if (files.sticker) {
    files.sticker.forEach((file) => deleteFile(file));
  }

  if (files.theme) {
    files.theme.forEach((file) => deleteFile(file));
  }
}

module.exports = {
  deleteFile,
  deleteFiles,
};
