class User {
  constructor(name, userType) {
    this.name = name;
    this.id = -1;
    this.userType = userType;
  }

  getName() {
    return this.name;
  }

  setId(id) {
    this.id = id;
  }

}
