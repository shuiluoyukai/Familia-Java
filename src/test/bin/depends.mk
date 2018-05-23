# Install dependencies

URL=http://raw.githubusercontent.com/ZeyuChen/third_party/master/package/
ifndef WGET
	WGET = wget --no-check-certificate
endif

# protobuf
PROTOBUF = ${DEPS_PATH}/include/google/protobuf/message.h
${PROTOBUF}:
	$(eval FILE=protobuf-2.5.0.tar.gz)
	$(eval DIR=protobuf-2.5.0)
	if [ ! -d "$(DIR)" ];\
	then \
		rm -rf $(FILE)  ;\
		$(WGET) -O $(FILE) $(URL)/$(FILE)  && tar -zxf $(FILE)  ;\
	fi
	cd $(DIR) && export CFLAGS=-fPIC && export CXXFLAGS=-fPIC && ./configure --disable-shared -prefix=$(DEPS_PATH) &&autoreconf -ivf && $(MAKE) && $(MAKE) install
protobuf: | ${PROTOBUF}

GFLAGS = ${DEPS_PATH}/include/google/gflags.h
${GFLAGS}:
	$(eval FILE=gflags-2.0-no-svn-files.tar.gz)
	$(eval DIR=gflags-2.0)
	if [ ! -d "$(DIR)" ];\
	then \
		rm -rf $(FILE)  ;\
		$(WGET) -O $(FILE) $(URL)/$(FILE)  && tar -zxf $(FILE)  ;\
	fi
	cd $(DIR) && export CFLAGS=-fPIC && export CXXFLAGS=-fPIC && ./configure -prefix=$(DEPS_PATH) &&autoreconf -ivf && $(MAKE) && $(MAKE) install
gflags: | ${GFLAGS}

# glog
GLOGS = ${DEPS_PATH}/include/glog/logging.h
${GLOGS}:
	$(eval FILE=glog-0.3.4.tar.gz)
	$(eval DIR=glog-0.3.4)
	if [ ! -d "$(DIR)" ];\
	then \
		rm -rf $(FILE)  ;\
		$(WGET) -O $(FILE) $(URL)/$(FILE)  && tar -zxf $(FILE)  ;\
	fi
	cd $(DIR) && export CFLAGS=-fPIC && export CXXFLAGS=-fPIC && ./configure -prefix=$(DEPS_PATH) --with-gflags=$(DEPS_PATH) &&autoreconf -ivf && $(MAKE) && $(MAKE) install
glog: | ${GLOGS}
